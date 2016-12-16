package Plugin.Test.Task;

import Core.Http.Job;
import Core.Task;

/**
 * Created by teddy on 10/06/2016.
 */
@Task()
public class TestTask extends Job {
    @Override
    public void task() {
        System.out.println("test task");
    }
}
