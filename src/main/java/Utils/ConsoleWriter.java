package Utils;

import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class ConsoleWriter<T> implements ItemWriter<T> {

    @Override
    public void write(List<? extends T> items) throws Exception {
        for (T item : items) {
            System.out.println(item);
        }
    }
//5min 1.55m records
    //https://aboullaite.me/spring-batch-tutorial-with-spring-boot/
//https://github.com/EBIvariation/examples/blob/master/spring-batch-dynamic-workflow/src/main/java/embl/ebi/variation/examples/dynamicworkflow/SimpleDeciderConfiguration.java
}


////////////////////////usefull in general with paterns///
//https://blog.codecentric.de/en/2013/06/spring-batch-2-2-javaconfig-part-1-a-comparison-to-xml/
//https://blog.codecentric.de/en/2013/06/spring-batch-2-2-javaconfig-part-2-jobparameters-executioncontext-and-stepscope/
//https://blog.codecentric.de/en/2013/06/spring-batch-2-2-javaconfig-part-3-profiles-and-environments/
//https://blog.codecentric.de/en/2013/06/spring-batch-2-2-javaconfig-part-4-job-inheritance/
//https://blog.codecentric.de/en/2013/06/spring-batch-2-2-javaconfig-part-5-modular-configurations/
//https://blog.codecentric.de/en/2013/07/spring-batch-2-2-javaconfig-part-6-partitioning-and-multi-threaded-step/


//https://gist.github.com/joshlong/5441496
//https://stackoverflow.com/questions/43083365/spring-batch-partition-step-exhaust-all-taskexecutor
//https://stackoverflow.com/questions/29107607/best-spring-batch-scaling-strategy
//https://stackoverflow.com/questions/37238813/spring-batch-looping-a-reader-processor-writer-step

//https://github.com/mminella/batch-docker
//https://stackoverflow.com/questions/25617962/how-does-the-singleton-bean-serve-the-concurrent-request
//http://www.bigsoft.co.uk/blog/index.php/2009/11/27/rules-of-a-threadpoolexecutor-pool-size
//https://www.infoq.com/articles/Java-Thread-Pool-Performance-Tuning
//https://stackoverflow.com/questions/38780796/how-does-spring-batch-step-scope-work
//https://stackoverflow.com/questions/29286699/repeating-a-step-x-times-in-spring-batch


////RabitMQ///
//https://stackoverflow.com/questions/22850546/cant-access-rabbitmq-web-management-interface-after-fresh-install


//RESTART JOB
//https://stackoverflow.com/questions/38846457/how-can-you-restart-a-failed-spring-batch-job-and-let-it-pick-up-where-it-left-o
//https://stackoverflow.com/questions/39393586/spring-boot-spring-batch-configuration-disable-the-automatic-creation-of-the
//https://www.synyx.de/blog/bean-x-of-type-y-is-not-eligible-for-getting-processed-by-all-beanpostprocessors/
//https://stackoverflow.com/questions/27570119/spring-batch-with-java-configuration
//http://www.javainuse.com/spring/bootbatch  DIT IS EEN PRACHTIG VOORBEELD VOOR REST MET EEN JOBLAUNCHER