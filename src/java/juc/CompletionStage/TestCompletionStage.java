package juc.CompletionStage;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;

public class TestCompletionStage {

    //一.根据阶段正常完成结果的产出型
    @Test
    public void thenApply() {
        CompletableFuture<String> stage = CompletableFuture.supplyAsync(() -> "hello");

        String result = stage.thenApply(s -> s + " world").join();
        System.out.println(result);
    }

    @Test
    public void thenCombine() {
        String result = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "hello";
        }).thenCombine(CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "world";
        }), (s1, s2) -> s1 + " " + s2).join();
        System.out.println(result);
    }

    @Test
    public void applyToEither() {
        String result = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Tom";
        }).applyToEither(CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "John";
        }), s -> "hello " + s).join();
        System.out.println(result);
    }

    //二.根据阶段正常完成结果的消费型（不会对阶段结果产生影响）
    @Test
    public void thenAccept(){
        CompletableFuture.supplyAsync(()->"hello").thenAccept(s -> System.out.println(s + " world"));
    }

    @Test
    public void thenAcceptBoth(){
        CompletableFuture.supplyAsync(()->{
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "hello";
        }).thenAcceptBoth(CompletableFuture.supplyAsync(()->{
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "world";
        }),(s1,s2)-> System.out.println(s1 + " " + s2));

        //等待打印结果
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
     public void acceptEither() {
        CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "hello john";
        }).acceptEither(CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "hello tom";
        }), System.out::println);

        //等待打印结果
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //三.只要求依赖的阶段正常完成的不产出也不消费型
    @Test
    public void thenRun(){
        CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "hello";
        }).thenRun(() -> System.out.println("hello world"));
        while (true){}
    }

    @Test
    public void runAfterBoth(){
        //不关心这两个CompletionStage的结果，只关心这两个CompletionStage正常执行完毕，之后在进行操作（Runnable）。
        CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "s1";
        }).runAfterBoth(CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "s2";
        }), () -> System.out.println("hello world"));
        while (true){}
    }

    @Test
    public void runAfterEither() {
        //两个CompletionStage，任何一个正常完成了都会执行下一步的操作（Runnable）。
        CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "s1";
        }).runAfterEither(CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "s2";
        }), () -> System.out.println("hello world"));
        while (true) {
        }
    }

    //四.只根据阶段正常完成的本身而不是结果的产出型
    @Test
    public void thenCompose(){
        String result = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "hello";
        }).thenCompose(s -> CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return s + " world";
        })).join();

        System.out.println(result);
    }

    //五.不论阶段是正常完成还是异常完成的消耗型
    @Test
    public void whenComplete(){
        String result = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (1 == 1) {
                throw new RuntimeException("测试一下异常情况");
            }

            return "hello ";
        }).thenCombine(CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("return world...");  //会执行
            return "world";
        }), (s1, s2) -> {
            String s = s1 + " " + s2;   //并不会执行
            System.out.println("combine result :"+s); //并不会执行
            return s;
        }).whenComplete((s, t) -> {
            System.out.println("current result is :" +s);
            if(t != null){
                System.out.println("阶段执行过程中存在异常：");
                t.printStackTrace();
            }
        }).join();

        System.out.println("final result:"+result); //并不会执行
    }

    //六.不论阶段是正常完成还是异常完成的产出型
    @Test
    public void handle() {
        String result = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //出现异常
            if (1 == 3) {
                throw new RuntimeException("测试一下异常情况");
            }
            return "Tom";
        }).handle((s, t) -> {
            if (t != null) { //出现异常了
                return "John";
            }
            return s; //这里也可以对正常结果进行转换
        }).join();
        System.out.println(result);
    }

    //七.异常完成的产出型
    @Test
    public void exceptionally() {
        String result = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (1 == 1) {
                throw new RuntimeException("测试一下异常情况");
            }
            return "s1";
        }).exceptionally(e -> {
            e.printStackTrace(); //e肯定不会null
            return "hello world"; //补偿返回
        }).join();
        System.out.println(result); //打印hello world
    }

    //八.实现该接口b不同实现之间互操作的类型转换方法
    @Test
    public void toCompletableFuture(){
        CompletableFuture uCompletableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("hello");
            return null;
        }).toCompletableFuture();
    }

}
