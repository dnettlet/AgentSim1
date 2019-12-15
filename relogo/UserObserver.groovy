package cancercells.relogo

import static repast.simphony.relogo.Utility.*;
import static repast.simphony.relogo.UtilityG.*;
import repast.simphony.relogo.Stop;
import repast.simphony.relogo.Utility;
import repast.simphony.relogo.UtilityG;
import repast.simphony.relogo.schedule.Go;
import repast.simphony.relogo.schedule.Setup;
import cancercells.ReLogoObserver;

class UserObserver extends ReLogoObserver{

@Setup
	def setup(){
		clearAll()
		createCCells(GlobalVar.ccell_init_quantity){
			def x = randomXcor()
			def y = randomYcor()
			while(x*x + y*y > 40*50) {
				x = randomXcor()
				y = randomYcor()
			}
			setxy(x,y)
			setColor(red())
		}
		createNCells(GlobalVar.ncell_init_quantity){
			def x = randomXcor()
			def y = randomYcor()
			setxy(randomXcor(),randomYcor())
			setColor(green())
		}
		createNKCells(GlobalVar.nkcell_init_quantity){
			def x = randomXcor()
			def y = randomYcor()
			while(x*x + y*y > 40*50) {
				x = randomXcor()
				y = randomYcor()
			}
			setxy(x,y)              
			setColor(white())
			setExperiment()
		}
		
	}

	@Go
	def go(){
		GlobalVar.ccellsbefore = count(CCells())
		ask(CCells()) {	step()	}
		ask(NKCells()){	step()	}
		ask(NCells()) {	step()	}
		
		GlobalVar.ccellsafter = count(CCells())
		
		
		printf("CCell:  " + count(CCells()) + "\n")
		printf("NKCell: " + count(NKCells()) + "\n")

		
		//if(GlobalVar.ccellsafter > GlobalVar.ccellsbefore && GlobalVar.ccellsbefore < 300)
			//stop()
	}


	def numbOfCCells() { return count(CCells()) }
	def numbOfNCells() { return count(NCells()) }
	def numbOfNKCells() { return count(NKCells()) }
}