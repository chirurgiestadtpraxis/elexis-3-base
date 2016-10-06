/*******************************************************************************
 * Copyright (c) 2011, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 *******************************************************************************/
package at.medevit.elexis.gdt.command;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

import at.medevit.elexis.gdt.constants.GDTConstants;
import at.medevit.elexis.gdt.constants.GDTPreferenceConstants;
import at.medevit.elexis.gdt.handler.GDTOutputHandler;
import at.medevit.elexis.gdt.interfaces.IGDTCommunicationPartner;
import at.medevit.elexis.gdt.messages.GDTSatzNachricht6302;
import at.medevit.elexis.gdt.tools.GDTSatzNachrichtHelper;
import at.medevit.elexis.gdt.ui.dialog.NeueUntersuchungAnfordernDialog;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.data.Patient;

public class NeueUntersuchungAnfordern extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		Patient pat = null;
		
		ISelection selection =
			HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection strucSelection = (IStructuredSelection) selection;
			pat = (Patient) strucSelection.getFirstElement();
		}
		
		String configuredGDTId = CoreHub.localCfg.get(GDTPreferenceConstants.CFG_GDT_ID, "");
		if (pat == null)
			pat = ElexisEventDispatcher.getSelectedPatient();
		
		if(pat==null) {
			return null;
		}
		
		GDTSatzNachricht6302 gdt6302 =
			new GDTSatzNachricht6302(pat.get(Patient.FLD_PATID), pat.getName(), pat.getVorname(),
				pat.getGeburtsdatum(), null, pat.get(Patient.TITLE), null, pat.get(Patient.FLD_ZIP)
					+ " " + pat.get(Patient.FLD_PLACE), pat.get(Patient.FLD_STREET), null,
				GDTSatzNachrichtHelper.bestimmeGeschlechtsWert(pat.get(Patient.SEX)), null, null,
				null, null, null, null, configuredGDTId, GDTConstants.ZEICHENSATZ_IBM_CP_437 + "",
				GDTConstants.GDT_VERSION);
		
		NeueUntersuchungAnfordernDialog nuad =
			new NeueUntersuchungAnfordernDialog(Display.getCurrent().getActiveShell(), gdt6302);
		int retVal = nuad.open();
		
		if (retVal == TitleAreaDialog.CANCEL)
			return null;
		
		IGDTCommunicationPartner cp = nuad.getGDTCommunicationPartner();
		GDTOutputHandler.handleOutput(gdt6302, cp);
		
		return null;
	}
	
}