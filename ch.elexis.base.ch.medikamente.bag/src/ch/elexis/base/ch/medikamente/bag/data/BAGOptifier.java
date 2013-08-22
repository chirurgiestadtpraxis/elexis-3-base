/*******************************************************************************
 * Copyright (c) 2007-2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *******************************************************************************/

package ch.elexis.base.ch.medikamente.bag.data;

import java.util.List;

import ch.elexis.core.data.Konsultation;
import ch.elexis.core.data.Verrechnet;
import ch.elexis.core.data.interfaces.IOptifier;
import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.rgw.tools.Result;

public class BAGOptifier implements IOptifier {
	
	public Result<Object> optify(final Konsultation kons){
		return new Result<Object>(kons);
	}
	
	public Result<IVerrechenbar> add(final IVerrechenbar code, final Konsultation kons){
		if (code instanceof BAGMedi) {
			List<Verrechnet> old = kons.getLeistungen();
			for (Verrechnet v : old) {
				IVerrechenbar vv = v.getVerrechenbar();
				if (vv.getCode().equals(code.getCode())) {
					v.changeAnzahl(v.getZahl() + 1);
					// v.setZahl(v.getZahl()+1);
					return new Result<IVerrechenbar>(code);
				}
				if (vv instanceof BAGMedi) {
					BAGMedi bm = (BAGMedi) vv;
					
				}
			}
			old.add(new Verrechnet(code, kons, 1));
			
		}
		return new Result<IVerrechenbar>(code);
	}
	
	public Result<Verrechnet> remove(final Verrechnet v, final Konsultation kons){
		List<Verrechnet> old = kons.getLeistungen();
		old.remove(v);
		v.delete();
		return new Result<Verrechnet>(null);
	}
	
}