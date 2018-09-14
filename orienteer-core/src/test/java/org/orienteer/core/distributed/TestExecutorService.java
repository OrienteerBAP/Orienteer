package org.orienteer.core.distributed;

import com.hazelcast.core.*;
import com.hazelcast.monitor.LocalExecutorStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class TestExecutorService implements IExecutorService {

    private static final Logger LOG = LoggerFactory.getLogger(TestExecutorService.class);

    @Override
    public void execute(Runnable command, MemberSelector memberSelector) {
        command.run();
    }

    @Override
    public void executeOnKeyOwner(Runnable command, Object key) {
        command.run();
    }

    @Override
    public void executeOnMember(Runnable command, Member member) {
        command.run();
    }

    @Override
    public void executeOnMembers(Runnable command, Collection<Member> members) {
        command.run();
    }

    @Override
    public void executeOnMembers(Runnable command, MemberSelector memberSelector) {
        command.run();
    }

    @Override
    public void executeOnAllMembers(Runnable command) {
        command.run();
    }

    @Override
    public <T> Future<T> submit(Callable<T> task, MemberSelector memberSelector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Future<T> submitToKeyOwner(Callable<T> task, Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Future<T> submitToMember(Callable<T> task, Member member) {
        FutureTask<T> futureTask = new FutureTask<>(task);
        futureTask.run();
        return futureTask;
    }

    @Override
    public <T> Map<Member, Future<T>> submitToMembers(Callable<T> task, Collection<Member> members) {
        Map<Member, Future<T>> map = new LinkedHashMap<>();
        for (Member member : members) {
            FutureTask<T> futureTask = new FutureTask<>(task);
            futureTask.run();
            map.put(member, futureTask);
        }
        return map;
    }

    @Override
    public <T> Map<Member, Future<T>> submitToMembers(Callable<T> task, MemberSelector memberSelector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Map<Member, Future<T>> submitToAllMembers(Callable<T> task) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> void submit(Runnable task, ExecutionCallback<T> callback) {
        task.run();
    }

    @Override
    public <T> void submit(Runnable task, MemberSelector memberSelector, ExecutionCallback<T> callback) {
        task.run();
    }

    @Override
    public <T> void submitToKeyOwner(Runnable task, Object key, ExecutionCallback<T> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> void submitToMember(Runnable task, Member member, ExecutionCallback<T> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void submitToMembers(Runnable task, Collection<Member> members, MultiExecutionCallback callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void submitToMembers(Runnable task, MemberSelector memberSelector, MultiExecutionCallback callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void submitToAllMembers(Runnable task, MultiExecutionCallback callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> void submit(Callable<T> task, ExecutionCallback<T> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> void submit(Callable<T> task, MemberSelector memberSelector, ExecutionCallback<T> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> void submitToKeyOwner(Callable<T> task, Object key, ExecutionCallback<T> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> void submitToMember(Callable<T> task, Member member, ExecutionCallback<T> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> void submitToMembers(Callable<T> task, Collection<Member> members, MultiExecutionCallback callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> void submitToMembers(Callable<T> task, MemberSelector memberSelector, MultiExecutionCallback callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> void submitToAllMembers(Callable<T> task, MultiExecutionCallback callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LocalExecutorStats getLocalExecutorStats() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getPartitionKey() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getServiceName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void destroy() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void shutdown() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Runnable> shutdownNow() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isShutdown() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isTerminated() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean awaitTermination(long l, TimeUnit timeUnit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Future<T> submit(Callable<T> callable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Future<T> submit(Runnable runnable, T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<?> submit(Runnable runnable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> collection) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> collection, long l, TimeUnit timeUnit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> collection) throws InterruptedException, ExecutionException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> collection, long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void execute(Runnable runnable) {
        runnable.run();
    }
}
