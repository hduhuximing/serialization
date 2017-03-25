package com.jfireframework.baseutil;

import java.util.Queue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.LinkedTransferQueue;
import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import com.jfireframework.baseutil.concurrent.MPSCQueue;
import com.jfireframework.baseutil.concurrent.SpscQueue;

@State(Scope.Thread)
public class BenchMarkForMpsc
{
    Queue<String> queue          = new MPSCQueue<String>();
    int           batchSize      = 1;
    int           threads        = 1;
    TimeValue     time           = TimeValue.milliseconds(300);
    int           iterations     = 20;
    int           warmIterations = 10;
    
    @Benchmark
    public void test() throws InterruptedException, BrokenBarrierException
    {
        queue.offer("sa");
    }
    
    @Test
    public void test1()
    {
        
        Options opt = new OptionsBuilder().include(NewBenchmark.class.getSimpleName())//
                .warmupIterations(warmIterations)//
                .warmupBatchSize(batchSize)//
                .warmupTime(time)//
                .threads(threads)//
                .measurementBatchSize(batchSize)//
                .measurementIterations(iterations)//
                .measurementTime(time)//
                 .shouldDoGC(true)//
                .forks(1).build();
        try
        {
            new Runner(opt).run();
        }
        catch (RunnerException e)
        {
            e.printStackTrace();
        }
    }
    
}
