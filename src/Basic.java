import krpc.client.Connection;
import krpc.client.RPCException;
import krpc.client.StreamException;
import krpc.client.services.KRPC;
import krpc.client.services.KRPC.Expression;
import krpc.client.services.SpaceCenter;
import krpc.schema.KRPC.Event;
import krpc.schema.KRPC.ProcedureCall;

import java.io.IOException;

public class Basic {
	public static void main(String[] args) throws IOException, RPCException, InterruptedException, StreamException {
		Connection conn = Connection.newInstance("ConnTest", "127.0.0.1", 50000, 50001);
		KRPC krpc = KRPC.newInstance(conn);
		SpaceCenter ksc = SpaceCenter.newInstance(conn);
		SpaceCenter.Vessel currentVessel = ksc.getActiveVessel();
		System.out.println("Throttle: " + currentVessel.getControl().getThrottle());
		currentVessel.getControl().activateNextStage();
		currentVessel.getAutoPilot().setSAS(true);
		currentVessel.getAutoPilot().targetPitchAndHeading(45, 90);
		Thread.sleep(1000);
		currentVessel.getControl().setThrottle((float) 1.0);
		Thread.sleep(1000);
		currentVessel.getControl().activateNextStage();
		Thread.sleep(20000);

		ProcedureCall srfAltitude = conn.getCall(currentVessel.flight(null), "getSurfaceAltitude");
		Expression expr = Expression.lessThan(conn, Expression.call(conn, srfAltitude),
				Expression.constantDouble(conn, 1000));
		krpc.client.Event event = krpc.addEvent(expr);
		synchronized (event.getCondition()) {
			event.waitFor();
		}
		currentVessel.getControl().activateNextStage();

		while (currentVessel.flight(currentVessel.getOrbit().getBody().getReferenceFrame()).getVerticalSpeed() < -0.1) {
			System.out.printf("Altitude = %.1f meters\n", currentVessel.flight(null).getSurfaceAltitude());
			Thread.sleep(1000);
		}
		System.out.println("Landed!");
		conn.close();
	}
}