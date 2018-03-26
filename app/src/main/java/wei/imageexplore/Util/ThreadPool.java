package wei.imageexplore.Util;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by wei on 2017/7/19.
 */
public class ThreadPool {
    //分别表示运行、停止、挂起状态
    public final static int  STATE_RUN=1,STATE_STOP=0,STATE_SUSPEND=2;
    protected final static int DEFAULT_SIZE=4;//默认开启4线程
    protected ArrayList<pThread> ThreadQueue; //线程队列
    protected int size;
    protected int pos=0;
    public ThreadPool(){
        this(DEFAULT_SIZE);
    }
    public ThreadPool(int size){
        if (size<1||size>16){
            size=DEFAULT_SIZE;
        }
        this.size=size;
        ThreadQueue=new ArrayList<>();
        for (int i = 0; i < size; i++) {
            pThread thread=new pThread();
            thread.start();
            ThreadQueue.add(thread);
        }
    }


    public  void execute(Runnable runnable){//添加任务进任务队列
        ThreadQueue.get(pos++).execute(runnable);//并运行当前任务
        if (pos==ThreadQueue.size())
            pos=0;
    }

    public  int size(){//获取在等待的任务数目
        int size=0;
        for (pThread thread:ThreadQueue){
            size+=thread.size();
        }
        return size;
    }

    public void shutdown(){//关闭线程池
        for (pThread thread:ThreadQueue){
            thread.setState(STATE_STOP);
        }
    }

    public class pThread extends Thread{
        private int state;// 0退出,1运行,2挂起
        private LinkedList<Runnable> workerQueue;//任务队列
        public pThread(){
            state=STATE_SUSPEND;
            workerQueue=new LinkedList<>();
        }

        public synchronized int getWaits(){
            return workerQueue.size();
        }

        public void setState(int newstate){//设置当前线程状态
            synchronized (this) {
                this.notify();
                state=newstate;
            }
        }

        public int size(){
            return workerQueue.size();
        }

        public boolean isFree(){//判断是否空闲
            return state==STATE_SUSPEND;
        }

        public int State(){
            return state;
        }

        public void execute(Runnable runnable){//添加任务准备运行
            workerQueue.add(runnable);
            if (state!=STATE_RUN)
                this.setState(STATE_RUN);
        }

        @Override
        public void run() {
            try {
            while (state!=STATE_STOP){
                if (workerQueue.size()==0||state==STATE_SUSPEND){
                    state=STATE_SUSPEND;
                    synchronized (this){//队列无任务挂起
                        this.wait();
                    }
                }
                if (workerQueue.size()>0) {
                    workerQueue.removeLast().run();
                }
            }

            } catch (Exception e) {
                Log.e("ThreadPool","More Error!!!!!!!!!!!!!!");
            }
        }
    }

}
