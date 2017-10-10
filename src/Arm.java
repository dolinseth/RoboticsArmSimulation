import java.text.DecimalFormat;
import java.util.*;

public class Arm {
	public float vertMoveSpeed; //the speed at which the arm should move vertically, in units per second
	public float horizMoveSpeed; //the speed at which the arm should move horizontall, in units per second
	
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
	
	public Arm(double UpperArmLength, double LowerArmLength, double UpperArmAngle, double JointAngle){
		this.ang1 = (float)UpperArmAngle;
		this.ang2 = (float)JointAngle;
		this.ang3 = (float)(180 - ang1 - ang2);
		this.length1 = (float)UpperArmLength;
		this.length2 = (float)LowerArmLength;
		CalcCoords();
	}
	
	public void SetHorizMoveSpeed(double HorizMoveSpeed){
		this.horizMoveSpeed = (float)HorizMoveSpeed;
	}
	
	public void SetVertMoveSpeed(double VertMoveSpeed){
		this.vertMoveSpeed = (float)VertMoveSpeed;
	}
	
	public static void main(String[] arg){
		//declaring the decimal format object for normalizing the formatting of printed statements
		DecimalFormat df = new DecimalFormat("#.000000");
		//declaring the arm object to to be simulated
		Arm arm = new Arm(10, 5, 30, 60);
		arm.SetVertMoveSpeed(1.0);
		arm.SetHorizMoveSpeed(1.0);
		//declares the scaanner for allowing the user to control the pacing of the iteration
		Scanner scan = new Scanner(System.in);
				
		//declaring variables to calculate the change in coordinates every 
		//tick to make sure it's changing by the right amount
		float deltaX = (float)0.0;
		float deltaY = (float)0.0;
		float oldX = arm.x2;
		float oldY = arm.y2;
		
		//loop to allow the user to iterate the simulation with each press of the enter key
		System.out.println("Press enter to continue. Type \"q\" to quit");
		while (scan.nextLine() != "q"){
			//updating  the variables for change in coordinates
			deltaX = arm.x2 - oldX;
			deltaY = arm.y2 - oldY;
			oldX = arm.x2;
			oldY = arm.y2;
			
			//verifying that the langth of each piece of the arm is not 
			//changing because that has been a problem
			float lengthVerif1 = CalcDistance((float)0.0, (float)0.0, arm.x1, arm.y1);
			float lengthVerif2 = CalcDistance(arm.x1, arm.y1, arm.x2, arm.y2);
		
			//calculating the spin rates, integrating the position, 
			//and printing the position and change in position each tick
			arm.CalcHorizSpinRates();
			System.out.println(df.format(arm.x1) + "\t" + df.format(arm.y1) + "\t" + df.format(arm.x2) + "\t" + df.format(arm.y2) + "\t" + df.format(deltaX) + "\t" + df.format(deltaY));
			System.out.println(df.format(lengthVerif1) + "\t" + df.format(lengthVerif2));
			arm.IteratePos(0.01);
		}
		
		scan.close();
	}
	
	public void CalcVertSpinRates(){
		//calculates the derivatives of the angles so that the function can be
		//integrated each tick
		//for vertical movement
		dAng1 = (float)(vertMoveSpeed / 
				(length1 * cos(ang1) + length2 * cos(ang3)
						+ ((length2 * sin(ang3) - length1 * sin(ang1))
							/(tan(ang3)) )));
		
		dAng2 = (float)(dAng1
				* ( (length2 * sin(ang3) - length1 * sin(ang1))
						/ (length2 * sin(ang3)) ));
	}
	
	public void CalcHorizSpinRates(){
		//calculates the derivatives of the angles so that the function can be
		//integrated each tick
		//for horizontal movement
		dAng1 = (float)(horizMoveSpeed / 
				( (length2 * sin(ang3) - length1 * sin(ang1))
						+ (tan(ang3) * 
								(length1 * cos(ang1) + length2 * cos(ang3)))));
		
		dAng2 = (float)(dAng1 * 
				((length1 * cos(ang1) + length2 * cos(ang3))) /
					(length2 * cos(ang3)));
	}
	
	public void IteratePos(double Time){
		//integrates the function each tick through Euler's method
		ang1 += (dAng1 * Time);
		ang2 += (dAng2 * Time);
		CalcCoords();
	}
	
	public void CalcCoords(){
		//calculates the coordinates of each point given
		ang3 = (180 - ang2 - ang1);
		x1 = (float)Math.cos(ang1 * CONV) * length1;
		y1 = (float)Math.sin(ang1 * CONV) * length1;
		x2 = (float)(x1 + Math.cos(ang3 * CONV) * length2);
		y2 = (float)(y1 + Math.sin(ang3 * CONV) * length2);		
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
