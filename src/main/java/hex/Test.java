package hex;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: laullon
 * Date: 08-abr-2003
 * Time: 13:16:06
 */
public class Test
{
    private JFrame win;

    public Test() throws IOException
    {
        byte[] ar;
        ar=new byte[16*16*100];
        Arrays.fill(ar,(byte)0);

        //ByteArrayOutputStream bos=new ByteArrayOutputStream();
        //ObjectOutputStream oos=new ObjectOutputStream(bos);
        //oos.writeObject("dfasnvcxnz.,mvnmc,xznvmcxzmnvcmxzcccbnxz cz hajk vc jbcvj xbnzvc sbj cvxz,bcxjnzbcvjhs avcjz cxmzncvxz ");
        //ar=bos.toByteArray();

        win=new JFrame();
        win.getContentPane().add(new JHexEditor(ar));
        win.pack();
        win.show();
    }

    public void windowClosing(WindowEvent e)
    {
        System.exit(0);
    }



    public static void main(String[] arg) throws IOException
    {
        new Test();
    }
}
