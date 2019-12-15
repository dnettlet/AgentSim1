package cancercells.relogo

import static repast.simphony.relogo.Utility.*
import static repast.simphony.relogo.UtilityG.*

import cancercells.ReLogoTurtle
import repast.simphony.relogo.Plural
import repast.simphony.relogo.Stop
import repast.simphony.relogo.Utility
import repast.simphony.relogo.UtilityG
import repast.simphony.relogo.schedule.Go
import repast.simphony.relogo.schedule.Setup

class CCell extends ReLogoTurtle {
	
	enum E_mode{ MULTIPLY, DEFEND, MOVE, TRAVEL, ARRIVE, DIE }
	def m_state = E_mode.MOVE
	def m_speed = 0.003  // was 0.001
	def pattern = "0000 0011"
	//def m_hide_chemical = false
	//def m_MICA  = 0.0
	//def m_ULBP2 = 0.0
	//def m_HLAI  = 0.0
	def m_MULTIPLY_chance = 0.0  // was 0.0006
	
	def m_strategy_l_active = false;
	
	UserLink m_link_target_ccell = null;
	
	//Random randc = new Random();
	
	def f_multiply() {
		hatch(1){
			setxy(this.getXcor() ,this.getYcor())
			left(180)
			m_state = E_mode.MOVE
		}
		m_state = E_mode.MOVE
	}
		
	def f_move() {
		if(randomFloat(1) < m_MULTIPLY_chance) {
			m_state = E_mode.MULTIPLY
			return
		}
		moveTowards(null)
	}
	
	def f_defend() {	}
	def f_travel() {	}
	def f_arrive() {	}
	
	/*def moveTowards(def target) {
		if(randomFloat(1) < 0.5)
			left(randomNormal(0, 6))
		else
			right(randomNormal(0, 6))
		forward(m_speed)
	}*/
	def moveTowards(def target) {
		if(randomFloat(1) < 0.5)
			left(randomNormal(0, 6))
		else
			right(randomNormal(0, 6))
		forward(m_speed)
	}
	
	def step(){
		switch(m_state){
			case E_mode.MULTIPLY:	f_multiply();	break
			//case E_mode.DEFEND:		f_defend();		break
			case E_mode.MOVE:		f_move();		break
			//case E_mode.TRAVEL:		f_travel();		break
			//case E_mode.ARRIVE:		f_arrive();		break
	}}
}
