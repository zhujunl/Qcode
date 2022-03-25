package com.miaxis.common.runnable;

/**
 * @author Tank
 * @date 2020/10/10 9:45
 * @des
 * @updateAuthor
 * @updateDes
 */
public abstract class LoopRunnable<T> implements Runnable {

    private T data;

    public LoopRunnable() {
    }

    public LoopRunnable(T data) {
        this.data = data;
    }

    @Override
    public void run() {
        try {
            this.onRun(this.data);
        } catch (Exception e) {
            e.printStackTrace();
            this.onError(e);
        }
    }

    public void clear() {
        this.data = null;
    }

    public void onError(Exception e) {
    }

    public abstract void onRun(T data);

}
