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

class NCell extends ReLogoTurtle {

	enum E_mode{ MOVE, MULTIPLY  }
	def m_state = E_mode.MOVE
	def m_speed = 0.01
	def pattern = "0000 0001"
	def m_MULTIPLY_chance = 0.001  // was 5.0
	//Random randn = new Random();

	def f_move() {
		if(randomFloat(1) < m_MULTIPLY_chance) {
			m_state = E_mode.MULTIPLY
			return
		}
		moveTowards(null)
	}

	def moveTowards(def target) {
		if(target == null) left(randomNormal(0, 2)) else face(target)
		forward(this.m_speed)
	}
	
	def f_multiply() {
		m_state = E_mode.MOVE
		hatch(1){
			setxy(this.getXcor() ,this.getYcor())
			left(180)
			//m_state = E_mode.MOVE
		}
		//m_state = E_mode.MOVE
	}
	
	def step(){
		switch(m_state){
			case E_mode.MOVE:		f_move();		break
			case E_mode.MULTIPLY:	f_multiply();	break
	}}
}
