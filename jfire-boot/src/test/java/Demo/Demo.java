package Demo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import com.jfireframework.boot.AppInfo;
import com.jfireframework.boot.BootApplication;
import com.jfireframework.jfire.config.annotation.PackageNames;

@PackageNames("Demo")
@AppInfo(appName = "app")
public class Demo
{
    public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, UnrecoverableKeyException, KeyManagementException
    {
        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(new FileInputStream("z:/keystore/mykey.keystore"), "wwee1234".toCharArray());
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, "1234asdf".toCharArray());
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(keyManagerFactory.getKeyManagers(), null, null);
        new BootApplication(Demo.class,sslContext).start();
    }
}
