package Demo;

import com.jfireframework.boot.AppInfo;
import com.jfireframework.boot.BootApplication;

@AppInfo(appName = "app")
public class Demo
{
    public static void main(String[] args)
    {
        new BootApplication(Demo.class).start();
    }
}
