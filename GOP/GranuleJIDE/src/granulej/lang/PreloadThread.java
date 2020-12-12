package granulej.lang;

import granulej.gui.datastructure.GranuleUnit;
import gui.constant.GranuleConstant;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PreloadThread extends Thread{
    private static PreloadThread instance = null;
    public static synchronized PreloadThread getInstance()  {
        if (instance == null) {
            instance = new PreloadThread();
            instance.start();
        }
        return instance;
    }

    private Queue<Runnable> tasks = new LinkedList<Runnable>();
    private Queue<Runnable> highPriviledgeTasks = new LinkedList<Runnable>();
    private ConcurrentHashMap<String, GranuleUnit> similar_cache = new ConcurrentHashMap<String, GranuleUnit>();

    static final Lock lock = new ReentrantLock();
    static final Condition newTask = lock.newCondition();

    private Runnable pollTask() {
        lock.lock();
        try {
            while (true) {
                if (!highPriviledgeTasks.isEmpty()) {
                    return highPriviledgeTasks.poll();
                } else if (!tasks.isEmpty()) {
                    return tasks.poll();
                }
                newTask.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void run() {
        while (true) {
            Runnable r = pollTask();
            r.run();
        }
    }

    private void addTask(Queue<Runnable> q, Runnable run) {
        lock.lock();
        try {
            q.add(run);
            newTask.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private static class SearchParam {
        public String gName;
        public boolean ready = false;
        public GranuleUnit result = null;
        public SearchParam(String name) {
            gName = name;
        }
    }

    private void searchForSimilarGranule(SearchParam p) {
        Object[] similarGra = GranulePolling.getInstance().getPollingResult(p.gName, 0);
        if(similarGra[1].equals(GranuleConstant.GRANULE_NOT_EXIST))
        {
            System.out.println("no similar granule on granule server!!!");
            return ;
        }

        Class simiGranule=(Class)similarGra[0];
        String simiFileName=(String)similarGra[1];

        boolean isFit = InvokeAgent.executeGranule(simiGranule);
        GranuleUnit unit = new GranuleUnit(simiGranule,null,simiFileName);

        similar_cache.put(p.gName, unit);

        synchronized (p) {
            p.ready = true;
            p.result = unit;
            p.notifyAll();
        }
    }

    public void invalidateGranule(final GranuleNode node) {
        if (node.isDirty() && !node.isRemoved()) {
            addTask(tasks, new Runnable() {
                @Override
                public void run() {
                    if (node.isDirty() && !node.isRemoved()) {
                        try {
                            node.setStatus((Boolean)node.getFitMethod().invoke(null, null));
                            node.clearDirty();
                            if (!node.getStatus()) {
                                addTask(tasks, new Runnable() {
                                    @Override
                                    public void run() {
                                        searchForSimilarGranule(new SearchParam(node.getGranuleName()));
                                    }
                                });
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    public GranuleUnit searchForSimilarGranule(ArrayList<String> gNames) {
        long t0 = System.nanoTime();
        if(gNames == null || gNames.size()==0)
            return null;

        final String gName = gNames.get(0);

        GranuleUnit unit = similar_cache.get(gName);
        if (unit != null) {
            if (InvokeAgent.executeGranule(unit.simiGranule)) {
                long t1 = System.nanoTime();
                if (GranuleOptions.enableSearchTimeStat)
                    System.out.println("SearchTime: " + (t1 - t0));
                return unit;
            }
        }

        final SearchParam p = new SearchParam(gName);
        addTask(highPriviledgeTasks, new Runnable() {
            @Override
            public void run() {
                searchForSimilarGranule(p);
            }
        });

        try {
            synchronized (p) {
                while (!p.ready)
                    p.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long t1 = System.nanoTime();
        if (GranuleOptions.enableSearchTimeStat)
            System.out.println("SearchTime: " + (t1 - t0));

        return p.result;
    }

}
