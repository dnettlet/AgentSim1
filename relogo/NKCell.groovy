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

class NKCell extends ReLogoTurtle {
	
	enum E_mode{ LEARN, MOVE, ATTACK, MULTIPLY, DORMANT, WAKEUP }
	def m_state = E_mode.LEARN
	
	def m_speed = 0.01  // was 0.02 then 0.005
	def m_MULTIPLY_chance = 0.0006
	def m_kill_distance = 0.5 // was 1
	def m_lose_distance = 1.0 // was 3.5
	def m_kill_chance = 1.0
	
	// Number of steps without BioPlastic around this Bacteria
	def m_nsteps_no_ccells = 0
	def m_nsteps_no_ccells_for_dormant = 2
	
	def m_nsteps_with_ccells = 0
	def m_nsteps_with_ccells_for_wakeup = 2
	
	def m_pattern = "0000 0010"
	def born = 0
	def nccells = 0

	/********EXPERIMENTAL CONTROL PARAMETERS*******/	
	def m_RATIO   	= 1   // can be 8, 4, 2 or 1
	def m_RESTING 	= 0.0
	def m_IL15    	= 0.0
	def m_NKG2D   	= 0.0
	def m_MICA  	= 0.0
	def m_ULBP2 	= 0.0
	def m_HLAI  	= 1.0
	/**********************************************/
	
	def m_target_ccell = null
	
	def f_learn() {
		// NKcell learns how to to identify and kill CCells
		m_state = E_mode.MOVE
	}
	
	def f_move() {
		if(randomFloat(1) < m_MULTIPLY_chance) {
			m_state = E_mode.MULTIPLY
			return
		}
		
		def neighbor_ccells = inRadius(CCells(),m_lose_distance)
		def ccells_found = count(neighbor_ccells) > 0
		
		
		if(ccells_found)
		{
			CCell ccell = oneOf(neighbor_ccells)
			if(randomFloat(1) < m_kill_chance*GlobalVar.m_KILL_chance) {
				// NKcell finds a visible CCell and if 
				// not found just try again in the next tick
				m_target_ccell = ccell;
				createLinkTo(m_target_ccell)
				moveTowards(m_target_ccell)
				m_state = E_mode.ATTACK
				return
			}
		}
		
		// NKcell walks randomly
		moveTowards(null)
	}
	
	def f_multiply() {
		hatch(1){
			born = GlobalVar.cycles
			setxy(this.getXcor(), this.getYcor())
			left(180)
			m_state = E_mode.MOVE
		}
		m_state = E_mode.MOVE
	}
	
	def f_attack() {
			
		if(m_target_ccell == null) {
			m_state = E_mode.MOVE
			return
		}
		
		def link = link(this,m_target_ccell)
		if(link == null) {
			m_state = E_mode.MOVE
			return
		}
		
		def ccell_distance = link.linkLength()
		if(ccell_distance < m_kill_distance) {
			// NKcell selfdestructs along with the CCell
			m_target_ccell.die()
			m_state = E_mode.MOVE
			//hatch(1);  // dont proliferate the NKs
			return
		}
		else if(ccell_distance < m_lose_distance) {
			// NKcell pursues CCell
			moveTowards(m_target_ccell)
		}
		else {
			// NKcell losses sight of CCell
			link.die()
			m_state = E_mode.MOVE
		}
	}
	
	def hasCcellsNear() {
		def neighbor_ccells = inRadius(CCells(),m_lose_distance)
		return count(neighbor_ccells)
	}
	
	def f_dormant() {
		m_nsteps_with_ccells = 0;
		
		/*if(hasBioPlasticsNear()) {
			m_state = E_mode.MOVE
			GlobalVar.dormants = GlobalVar.dormants - 1;
			return
		}*/
	}
	
	def f_wakeup() {
		m_nsteps_no_ccells = 0;
		m_state = E_mode.MOVE
		
		/*if(hasBioPlasticsNear()) {
			m_state = E_mode.MOVE
			GlobalVar.dormants = GlobalVar.dormants - 1;
			return
		}*/
	}
	
	def moveTowards(def target) {
		if(target == null) left(randomNormal(0, 6)) else face(target)   // was 0, 2
		forward(m_speed)
	}
	
	def setExperiment()
	{
		def targetpercent = 0.0
		def target = 0.0
		def current_value = 0.0
		def diff = 0.0
		def diffpercent = 0.0
		
		/*
		 * EXPERIMENT	RESTING	IL15	ULBP2	MICA	NKG2D	HLAI	RATIOS		TARGETS (per ratio)
		 * 1			1		0		0		0		0		0		1,2,4,8		0.14, 0.16, 0.19, 0.26
		 * 2			0		1		0		0		0		0					0.19, 0.27, 0.40, 0.45
		 * 3			0		0		1		1		1		0					0.10, 0.10, 0.11, 0.13
		 * 4			0		0		0		0		0		1					0.42, 0.55, 0.66, 0.73				
		 * 5			0		1		0		0		0		1					0.41, 0.57, 0.74, 0.87
		 */
		
		int[][] experiments = new int[5][6];
		experiments[0][0]=1;experiments[0][1]=0;experiments[0][2]=0;experiments[0][3]=0;experiments[0][4]=0;experiments[0][5]=0;
		experiments[1][0]=0;experiments[1][1]=1;experiments[1][2]=0;experiments[1][3]=0;experiments[1][4]=0;experiments[1][5]=0;
		experiments[2][0]=0;experiments[2][1]=0;experiments[2][2]=1;experiments[2][3]=1;experiments[2][4]=1;experiments[2][5]=0;
		experiments[3][0]=0;experiments[3][1]=0;experiments[3][2]=0;experiments[3][3]=0;experiments[3][4]=0;experiments[3][5]=1;
		experiments[4][0]=0;experiments[4][1]=1;experiments[4][2]=0;experiments[4][3]=0;experiments[4][4]=0;experiments[4][5]=1;
		
		
		int experiment_id = 4;  // IMPORTANT. THIS CHOOSES THE EXPERIMENT ! goes from 0 to 4
		
		/***** EXPERIMENT 1 *****/									// 0.14, 0.16, 0.19, 0.26
		//1:1 0.36  gave 343 and should be 344 * (redone)
		//2:1 0.3 gave 335 and should be 336 * (redone)
		//4:1 0.25 gave 328 and should be 324 * (redone)
		//8:1 0.24 gave 286 and should be 296 * (redone)
		/***** EXPERIMENT 2 *****/									// 0.19, 0.27, 0.40, 0.45
		//1:1 0.4 gave 319 and should be 324 * (redone)
		//2:1 0.35 gave 286 and should be 293 * (redone)
		//4:1 0.3 gave 244 and should be 240 * (redone)
		//8:1 0.25 gave 214 and should be 220 * (redone)
		/***** EXPERIMENT 3 *****/ 									// 0.10, 0.10, 0.11, 0.13
		//1:1 0.72 gave 364 and should be 360 *
		//2:1 0.68 gave 367 and should be 360 *
		//4:1 0.65 gave 346 and should be 356 *
		//8:1 0.61 gave 346 and should be 348 *
		/***** EXPERIMENT 4 *****/ 									// 0.42, 0.55, 0.66, 0.73
		//1:1 0.56 gave 236 and should be 232 * (done)
		//2:1 0.50 gave 184 and should be 180 * (done)
		//4:1 0.445 gave 137 and should be 136 * (done)
		//8:1 0.345 gave 105 and should be 108 * (done)
		/***** EXPERIMENT 5 *****/ 									// 0.41, 0.57, 0.74, 0.87
		//1:1 0.73 gave 230 and should be 236 * (done)
		//2:1 0.685 gave 175 and should be 172 * (done)
		//4:1 0.643 gave 108 and should be 104 * (done)
		//8:1 0.62 gave 54 and should be 52 * (done)
		
		float[][] weights = new float[6][5];
		weights[0][0]=1.0;weights[0][1]=1.0;weights[0][2]=1.0;weights[0][3]=1.0;weights[0][4]=1.0; 	// feature 1, RESTING
		weights[1][0]=1.1;weights[1][1]=1.1;weights[1][2]=1.1;weights[1][3]=1.1;weights[1][4]=1.1; 	// feature 2, IL15
		weights[2][0]=0.9;weights[2][1]=0.9;weights[2][2]=0.9;weights[2][3]=0.9;weights[2][4]=0.9;  // feature 3, ULBP2
		weights[3][0]=0.9;weights[3][1]=0.9;weights[3][2]=0.9;weights[3][3]=0.9;weights[3][4]=0.9; 	// feature 4, MICA
		weights[4][0]=0.9;weights[4][1]=0.9;weights[4][2]=0.9;weights[4][3]=0.9;weights[4][4]=0.9; 	// feature 5, NKG2D
		weights[5][0]=1.1;weights[5][1]=1.1;weights[5][2]=1.1;weights[5][3]=1.1;weights[5][4]=1.1; 	// feature 6, HLAI
		
		int i=0, j=0
		
		for (i=0;i<6;++i)
			for (j=0;j<5;++j)
				weights[i][j]=weights[i][j]*((GlobalVar.RATIO*0.62)/(GlobalVar.RATIO))
		
		if (experiments[experiment_id][0] == 1)  
		{
			m_kill_chance   	= m_kill_chance   	* weights[0][0];
			m_kill_distance 	= m_kill_distance 	* weights[0][1];
			m_lose_distance 	= m_lose_distance 	* weights[0][2];
			m_speed 			= m_speed 		  	* weights[0][3];
			m_MULTIPLY_chance 	= m_MULTIPLY_chance * weights[0][4];
		}
		if (experiments[experiment_id][1] == 1) 
		{
			m_kill_chance   	= m_kill_chance   	* weights[1][0];
			m_kill_distance 	= m_kill_distance 	* weights[1][1];
			m_lose_distance 	= m_lose_distance 	* weights[1][2];
			m_speed 			= m_speed 		  	* weights[1][3];
			m_MULTIPLY_chance 	= m_MULTIPLY_chance * weights[1][4];
		}
		if (experiments[experiment_id][2] == 1)
		{
			m_kill_chance   	= m_kill_chance   	* weights[2][0];
			m_kill_distance 	= m_kill_distance 	* weights[2][1];
			m_lose_distance 	= m_lose_distance 	* weights[2][2];
			m_speed 			= m_speed 		  	* weights[2][3];
			m_MULTIPLY_chance 	= m_MULTIPLY_chance * weights[2][4];
		}
		if (experiments[experiment_id][3] == 1)
		{
			m_kill_chance   	= m_kill_chance   	* weights[3][0];
			m_kill_distance 	= m_kill_distance 	* weights[3][1];
			m_lose_distance 	= m_lose_distance 	* weights[3][2];
			m_speed 			= m_speed 		  	* weights[3][3];
			m_MULTIPLY_chance 	= m_MULTIPLY_chance * weights[3][4];
		}
		if (experiments[experiment_id][4] == 1)
		{
			m_kill_chance   	= m_kill_chance   	* weights[4][0];
			m_kill_distance 	= m_kill_distance 	* weights[4][1];
			m_lose_distance 	= m_lose_distance 	* weights[4][2];
			m_speed 			= m_speed 		  	* weights[4][3];
			m_MULTIPLY_chance 	= m_MULTIPLY_chance * weights[4][4];
		}
		if (experiments[experiment_id][5] == 1)
		{
			m_kill_chance   	= m_kill_chance   	* weights[5][0];
			m_kill_distance 	= m_kill_distance 	* weights[5][1];
			m_lose_distance 	= m_lose_distance 	* weights[5][2];
			m_speed 			= m_speed 		  	* weights[5][3];
			m_MULTIPLY_chance 	= m_MULTIPLY_chance * weights[5][4];
		}
	
	}
	
	def step()
	{
		nccells = hasCcellsNear()
		
		if (nccells == 0)
			m_nsteps_no_ccells = m_nsteps_no_ccells + 1
		else if (m_nsteps_no_ccells > 0)
			m_nsteps_no_ccells = m_nsteps_no_ccells - 1
		
		if(m_nsteps_no_ccells > m_nsteps_no_ccells_for_dormant && m_state != E_mode.DORMANT) {
			m_state = E_mode.DORMANT
			GlobalVar.dormants = GlobalVar.dormants + 1
		}
			
		if (nccells > 0)
			m_nsteps_with_ccells = m_nsteps_with_ccells + 1
		else if (m_nsteps_with_ccells > 0)
			m_nsteps_with_ccells = m_nsteps_with_ccells - 1

		
		if(m_nsteps_with_ccells > m_nsteps_with_ccells_for_wakeup && m_state == E_mode.DORMANT) {
			m_state = E_mode.WAKEUP
			m_state = E_mode.MOVE
			GlobalVar.dormants = GlobalVar.dormants - 1
		}
		
		//printf("m_nsteps_no_ccells:  " + m_nsteps_no_ccells + "\n")
		//printf("m_nsteps_with_ccells:  " + m_nsteps_with_ccells + "\n")
		
		switch(m_state){
			case E_mode.MULTIPLY:	f_multiply(); 	break
			case E_mode.LEARN:		f_learn();		break
			case E_mode.MOVE:		f_move();		break
			case E_mode.ATTACK:		f_attack();		break
			case E_mode.DORMANT:	f_dormant();	break
			case E_mode.WAKEUP:	    f_wakeup();	    break
	}}
}
