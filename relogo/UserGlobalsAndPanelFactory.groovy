package cancercells.relogo

import repast.simphony.relogo.factories.AbstractReLogoGlobalsAndPanelFactory

public class UserGlobalsAndPanelFactory extends AbstractReLogoGlobalsAndPanelFactory{
	public void addGlobalsAndPanelComponents(){
		
		addMonitorWL("numbOfCCells", "Number of C-Cells", 0.01)
		addMonitorWL("numbOfNCells", "Number of N-Cells", 0.01)
		addMonitorWL("numbOfNKCells", "Number of NK-Cells", 0.01)
	}
}