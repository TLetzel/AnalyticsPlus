/*
 * Copyright 2015-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.hswt.anap.service.standards;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.hswt.anap.model.ColumnConfiguration;
import de.hswt.anap.model.CompoundsDeclaration;
import de.hswt.anap.model.SolvingCompoundConfiguration;
import de.hswt.anap.model.SubstanceConfiguration;
import de.hswt.anap.service.standards.api.IStandardsService;
import de.hswt.hplcsl.Column;
import de.hswt.hplcsl.Compound;
import de.hswt.hplcsl.Definition.Solvent;

@Component
public class StandardsServiceDefault implements IStandardsService {

	private static final String SUBSTANCES_FILE = "/data/substances.json";

	private static final String SOLVENTS_FILE = "/data/solvents.json";

	private static final String COLUMNS_FILE = "/data/columns.json";

	private static final String COMPOUNDS_FILE = "/data/compounds.json";

	private List<SubstanceConfiguration> substances = new ArrayList<>();

	@Autowired
	private ObjectMapper mapper;

	@Override
	public List<SubstanceConfiguration> getAvailableSubstances() {
		if (substances.isEmpty()) {
			try {
				substances = mapper.readValue(StandardsServiceDefault.class.getResourceAsStream(SUBSTANCES_FILE),
						new TypeReference<List<SubstanceConfiguration>>() {
						});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return substances;
	}

	@Override
	public List<SolvingCompoundConfiguration> getAvailableSolvingCompunds() {
		List<SolvingCompoundConfiguration> solvents = new ArrayList<>();
		try {
			solvents = mapper.readValue(StandardsServiceDefault.class.getResourceAsStream(SOLVENTS_FILE),
					new TypeReference<List<SolvingCompoundConfiguration>>() {
					});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return solvents;
	}

	@Override
	public List<ColumnConfiguration> getAvailableColumns() {
		List<ColumnConfiguration> columns = new ArrayList<>();
		try {
			columns = mapper.readValue(StandardsServiceDefault.class.getResourceAsStream(COLUMNS_FILE),
					new TypeReference<List<ColumnConfiguration>>() {
					});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return columns;
	}

	@Override
	public List<CompoundsDeclaration> getCompoundDeclarations() {
		List<CompoundsDeclaration> compounds = new ArrayList<>();
		try {
			compounds = mapper.readValue(StandardsServiceDefault.class.getResourceAsStream(COMPOUNDS_FILE),
					new TypeReference<List<CompoundsDeclaration>>() {
					});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return compounds;
	}

	@Override
	public void initFiles() {
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		createSubstanceAgilentJson();
		createSubstanceWatersJson();
		createSolventJson();
		createColumnJson();
		createCompoundJson();
	}

	private void createColumnJson() {
		List<ColumnConfiguration> columns = new ArrayList<>();

		List<Double> particleSizes = new ArrayList<>();
		List<Double> diameters = new ArrayList<>();
		List<Double> lengths = new ArrayList<>();

		particleSizes.add(1.8);
		particleSizes.add(3.0);
		particleSizes.add(5.0);
		particleSizes.add(10.0);

		diameters.add(2.1);
		diameters.add(4.6);

		lengths.add(100.0);
		lengths.add(250.0);

		columns.add(new ColumnConfiguration("Agilent Zorbax SB-C18", particleSizes, diameters, lengths));

		particleSizes = new ArrayList<>();
		diameters = new ArrayList<>();
		lengths = new ArrayList<>();

		particleSizes.add(1.8);
		particleSizes.add(3.0);
		particleSizes.add(5.0);
		particleSizes.add(10.0);

		diameters.add(2.1);
		diameters.add(4.6);

		lengths.add(100.0);
		lengths.add(250.0);

		columns.add(new ColumnConfiguration("Waters Equity BEH C18", particleSizes, diameters, lengths));

		try {
			File file = new File("./columns.json");
			mapper.writeValue(file, columns);
			System.out.println(file.getAbsolutePath());
			System.out.println(mapper.writeValueAsString(columns));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createSubstanceAgilentJson() {
		List<SubstanceConfiguration> substancesAgilentZorbax = new ArrayList<>();

		// SubstanceConfiguration substance = new
		// SubstanceConfiguration("n-benzyl formamide", 0.87);
		// substancesAgilentZorbax.add(substance);
		// substance = new SubstanceConfiguration("benzylalcohol", 1.21);
		// substancesAgilentZorbax.add(substance);
		// substance = new SubstanceConfiguration("3-phenyl propanol", 1.94);
		// substancesAgilentZorbax.add(substance);
		// substance = new SubstanceConfiguration("acetophenone", 1.53);
		// substancesAgilentZorbax.add(substance);
		// substance = new SubstanceConfiguration("benzonitrile", 1.83);
		// substancesAgilentZorbax.add(substance);
		// substance = new SubstanceConfiguration("p-chlorophenol", 2.27);
		// substancesAgilentZorbax.add(substance);
		// substance = new SubstanceConfiguration("metyhl benzonate", 1.98);
		// substancesAgilentZorbax.add(substance);
		// substance = new SubstanceConfiguration("bromobenzene", 2.74);
		// substancesAgilentZorbax.add(substance);
		// substance = new SubstanceConfiguration("n-butylbenzene", 3.82);
		// substancesAgilentZorbax.add(substance);
		// substance = new SubstanceConfiguration("etyhlbenzene", 2.93);
		// substancesAgilentZorbax.add(substance);

		try {
			File file = new File("./substances.json");
			mapper.writeValue(file, substancesAgilentZorbax);
			System.out.println(file.getAbsolutePath());
			System.out.println(mapper.writeValueAsString(substancesAgilentZorbax));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createSubstanceWatersJson() {

		List<SubstanceConfiguration> substancesWatersAcquity = new ArrayList<>();

		// SubstanceConfiguration substance = new
		// SubstanceConfiguration("butylparaben", 2.98);
		// substancesWatersAcquity.add(substance);
		// substance = new SubstanceConfiguration("propiophenone", 2.23);
		// substancesWatersAcquity.add(substance);
		// substance = new SubstanceConfiguration("etyhlparaben", 2.02);
		// substancesWatersAcquity.add(substance);
		// substance = new SubstanceConfiguration("ketoprofen", 0.64);
		// substancesWatersAcquity.add(substance);
		// substance = new SubstanceConfiguration("4-nitrophenol", 1.35);
		// substancesWatersAcquity.add(substance);
		// substance = new SubstanceConfiguration("acetanilide", 1.21);
		// substancesWatersAcquity.add(substance);
		// substance = new SubstanceConfiguration("acenaphthene", 3.53);
		// substancesWatersAcquity.add(substance);
		// substance = new SubstanceConfiguration("heptanophenone", 4.01);
		// substancesWatersAcquity.add(substance);
		// substance = new SubstanceConfiguration("octanophenone", 4.45);
		// substancesWatersAcquity.add(substance);
		// substance = new SubstanceConfiguration("ibuprofen", 1.71);
		// substancesWatersAcquity.add(substance);
		// //FIXME caffeine bugged in Chromatogram
		// substance = new SubstanceConfiguration("caffeine", -0.55);
		// substancesWatersAcquity.add(substance);

		try {
			File file = new File("./substances.json");
			mapper.writeValue(file, substancesWatersAcquity);
			System.out.println(file.getAbsolutePath());
			System.out.println(mapper.writeValueAsString(substancesWatersAcquity));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createSolventJson() {
		List<SolvingCompoundConfiguration> solvents = new ArrayList<>();

		SolvingCompoundConfiguration solvent = new SolvingCompoundConfiguration("pureH20-100%", -5, 1, 1, 2);
		solvents.add(solvent);
		solvent = new SolvingCompoundConfiguration("pureMeOH-100%", 0, 5, -2, 0);
		solvents.add(solvent);
		solvent = new SolvingCompoundConfiguration("pureACN-100%", 0, 5, -2, 0);
		solvents.add(solvent);
		solvent = new SolvingCompoundConfiguration("Octanol-100%", 2, 5, 0, 2);
		solvents.add(solvent);
		solvent = new SolvingCompoundConfiguration("H20-80%, MeOH-20%", -5, 2, 2, 3);
		solvents.add(solvent);
		solvent = new SolvingCompoundConfiguration("H20-80%, ACN-20%", -5, 2, 2, 3);
		solvents.add(solvent);
		solvent = new SolvingCompoundConfiguration("H20-50%, MeOh-50%", -3, 3, -5, 3);
		solvents.add(solvent);
		solvent = new SolvingCompoundConfiguration("H20-50%, ACN-50%", -3, 3, -5, -3);
		solvents.add(solvent);
		solvent = new SolvingCompoundConfiguration("H20-20%, MeOH-80%", -2, 4, -3, -2);
		solvents.add(solvent);
		solvent = new SolvingCompoundConfiguration("H20-20%, ACN-80%", -2, 4, -3, -2);
		solvents.add(solvent);
		solvent = new SolvingCompoundConfiguration("MeOH-80%, ACN-20%", 0, 5, -2, 0);
		solvents.add(solvent);
		solvent = new SolvingCompoundConfiguration("MeOH-50%, ACN-50%", 0, 5, -2, 0);
		solvents.add(solvent);
		solvent = new SolvingCompoundConfiguration("MeOH-20%, ACN-80%", 0, 5, -2, 0);
		solvents.add(solvent);
		try {
			File file = new File("./solvents.json");
			mapper.writeValue(file, solvents);
			System.out.println(file.getAbsolutePath());
			System.out.println(mapper.writeValueAsString(solvents));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createCompoundJson() {
		List<CompoundsDeclaration> compounds = new ArrayList<>();
		// Map<Column, Map<Solvent ,List<Compound>>> compounds = new
		// HashMap<>();
		for (int i = 0; i < Globals.StationaryPhaseArray.length; i++) {
			String columnName = Globals.StationaryPhaseArray[i];
			Column column = new Column();
			column.setName(columnName);

			// Map<Solvent, List<Compound>> cs = new HashMap<>();
			// cs.put(Solvent.ACETONITRILE, new ArrayList<>());
			// cs.put(Solvent.METHANOL, new ArrayList<>());
			// compounds.put(column, cs);
			//
			// for (int k = 0; k < Globals.CompoundNameArray[i].length; k++) {
			// Compound compound = new Compound();
			// compound.loadCompoundInfo(k, Solvent.ACETONITRILE, i);
			// cs.get(Solvent.ACETONITRILE).add(compound);
			// }
			// for (int k = 0; k < Globals.CompoundNameArray[i].length; k++) {
			// Compound compound = new Compound();
			// compound.loadCompoundInfo(k, Solvent.METHANOL, i);
			// cs.get(Solvent.METHANOL).add(compound);
			// }

			List<Compound> cs = new ArrayList<>();
			CompoundsDeclaration compound = new CompoundsDeclaration();
			compound.setColumn(column);
			compound.setSolvent(Solvent.ACETONITRILE);
			compound.setCompounds(cs);
			compounds.add(compound);

			for (int k = 0; k < Globals.CompoundNameArray[i].length; k++) {
				Compound c = new Compound();
				c.loadCompoundInfo(k, Solvent.ACETONITRILE, i);
				cs.add(c);
			}

			cs = new ArrayList<>();
			compound = new CompoundsDeclaration();
			compound.setColumn(column);
			compound.setSolvent(Solvent.METHANOL);
			compound.setCompounds(cs);

			for (int k = 0; k < Globals.CompoundNameArray[i].length; k++) {
				Compound c = new Compound();
				c.loadCompoundInfo(k, Solvent.METHANOL, i);
				cs.add(c);
			}
			compounds.add(compound);
		}
		try {
			File file = new File("./compounds.json");
			mapper.writeValue(file, compounds);
			System.out.println(file.getAbsolutePath());
			System.out.println(mapper.writeValueAsString(compounds));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
