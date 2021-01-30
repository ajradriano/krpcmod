import krpc.client.Connection;
import krpc.client.RPCException;
import krpc.client.services.KRPC;
import krpc.client.services.SpaceCenter;

import java.io.IOException;

public class Basic {
    public static void main(String[] args) throws IOException, RPCException, InterruptedException {
        Connection conn = Connection.newInstance("ConnTest", "127.0.0.1", 50000, 50001);
        KRPC krpc = KRPC.newInstance(conn);
        SpaceCenter ksc = SpaceCenter.newInstance(conn);
        SpaceCenter.Vessel currentVessel = ksc.getActiveVessel();
        System.out.println("Throttle: " + currentVessel.getControl().getThrottle());
        currentVessel.getControl().activateNextStage();
        currentVessel.getAutoPilot().setSAS(true);
        currentVessel.getAutoPilot().targetPitchAndHeading(45, 90);
        Thread.sleep(1000);
        currentVessel.getControl().setThrottle((float) 0.4);
        Thread.sleep(1000);
        currentVessel.getControl().activateNextStage();
        Thread.sleep(5000);
    }
}