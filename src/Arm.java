import java.text.DecimalFormat;
import java.util.*;

public class Arm {
	public float vertMoveSpeed;
	public float horizMoveSpeed;
	
	public final double CONV = (Math.PI / 180.0); //the conversion factor to go from degrees to radians
	
	public float ang1; //angle between upper arm and the horizontal
	public float ang2; //angle between upper and lower arm
	public float ang3; //angle between lower arm and horizontal
	public float dAng1; //derivative of ang1 with respect to time
	public float dAng2; //derivative of ang2 with respect to time
	
	public float length1; //length of upper arm
	public float length2; //length of lower arm
	
	public float x1; //x location of joint between upper and lower arm
	public float y1; //y location of joint between upper and lower arm
	public float x2; //x location of hand
	public float y2; //y location of hand
	
	public Arm(double UpperArmLength, double LowerArmLength, double UpperArmAngle, double JointAngle, double VerticalMovementSpeed){
		this.ang1 = (float)UpperArmAngle;
		this.ang2 = (float)JointAngle;
		this.ang3 = (float)(180 - ang1 - ang2);
		this.length1 = (float)UpperArmLength;
		this.length2 = (float)LowerArmLength;
		this.vertMoveSpeed = (float)VerticalMovementSpeed;
		CalcCoords();
	}
	
	public static void main(String[] arg){
		DecimalFormat df = new DecimalFormat("#.000000");
		Arm arm = new Arm(10, 5, 30, 60, 1.0);
		Scanner scan = new Scanner(System.in);
		
		System.out.println("Press enter to continue. Type \"q\" to quit");
		float deltaX = (float)0.0;
		float deltaY = (float)0.0;
		float oldX = arm.x2;
		float oldY = arm.y2;
		
		while (scan.nextLine() != "q"){
			deltaX = arm.x2 - oldX;
			deltaY = arm.y2 - oldY;
			oldX = arm.x2;
			oldY = arm.y2;
			
			float lengthVerif1 = CalcDistance((float)0.0, (float)0.0, arm.x1, arm.y1);
			float lengthVerif2 = CalcDistance(arm.x1, arm.y1, arm.x2, arm.y2);
			
			arm.CalcSpinRates();
			System.out.println(df.format(arm.x1) + "\t" + df.format(arm.y1) + "\t" + df.format(arm.x2) + "\t" + df.format(arm.y2) + "\t" + df.format(deltaX) + "\t" + df.format(deltaY));
			System.out.println(df.format(lengthVerif1) + "\t" + df.format(lengthVerif2));
			arm.IteratePos(0.1);
		}
		
		scan.close();
	}
	
	public void CalcSpinRates(){
		//calculates the derivatives of the angle so that the function can be
		//integrated each tick
		
		//the conversion from written math to typed math is correct
		//so why the fuck doesn't it work?
		dAng1 = (float)(vertMoveSpeed / 
				(length1 * cos(ang1) + length2 * cos(ang3)
						+ ((length2 * sin(ang3) - length1 * sin(ang1))
							/(tan(ang3)) )));
		
		dAng2 = (float)(dAng1
				* ( (length2 * sin(ang3) - length1 * sin(ang1))
						/ (length2 * sin(ang3)) ));
	}
	
	public void IteratePos(double Time){
		//integrates the function each tick through Euler's method
		ang1 += (dAng1 * Time);
		ang2 += (dAng2 * Time);
		CalcCoords();
	}
	
	public void CalcCoords(){
		//calculates the coordinates of each point given
		x1 = (float)Math.cos(ang1 * CONV) * length1;
		y1 = (float)Math.sin(ang1 * CONV) * length1;
		x2 = (float)(x1 + Math.cos(ang2 * CONV) * length2);
		y2 = (float)(y1 + Math.sin(ang2 * CONV) * length2);		
	}
	
	public static float CalcDistance(float x1, float y1, float x2, float y2){
		//calculates the distance between two points
		//used for verifying that the length of the sides are the same each frame
		float result = (float)Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
		return result;
	}
	
	//custom trig functions that use degrees instead of radians
	public float cos(float theta){
		return (float)Math.cos(theta * CONV);
	}
	
	public float sin(float theta){
		return (float)Math.sin(theta * CONV);
	}
	
	public float tan(float theta){
		return (float)Math.tan(theta * CONV);
	}
}