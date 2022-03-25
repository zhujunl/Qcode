package com.miaxis.common.runnable;

/**
 * @author Tank
 * @date 2020/9/17 13:20
 * @des
 * @updateAuthor
 * @updateDes
 */
public abstract class SingleRunnable<T> extends LoopRunnable<T> {


    public SingleRunnable() {
    }

    public SingleRunnable(T data) {
        super(data);
    }

    @Override
    public void run() {
        super.run();
        clear();
    }
}
