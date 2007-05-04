package testlib.regressions.bug1022446;

import java.awt.*;
import java.util.concurrent.*;

/** 
 * A stripped down version of the generified SwingWorker to demonstrate the bug.
 * Unchanged from testlib v1. 
 */
public class Java5Demo<V>
{
    private final FutureTask<V> task = 
        new FutureTask<V>( 
                new Callable<V>() {
                    public V call() throws Exception {
                        return null;
                    }
                } 
        ) {
            protected void done() {
                EventQueue.invokeLater( new Runnable() {
                    public void run() {
                        // finished();
                    }
                } );
            }
        };
}