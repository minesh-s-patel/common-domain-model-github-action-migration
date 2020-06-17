package org.isda.cdm.processor;

import cdm.base.staticdata.asset.common.ISOCurrencyCodeEnum;
import com.regnosys.rosetta.common.translation.Mapping;
import com.regnosys.rosetta.common.translation.Path;
import com.rosetta.model.lib.path.RosettaPath;
import org.isda.cdm.ElectiveAmountElection;
import org.isda.cdm.Money;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.isda.cdm.processor.MappingProcessorUtils.*;

public class ElectiveAmountElectionMappingHelper {

	private static final String ZERO = "zero";

	private final RosettaPath path;
	private final List<Mapping> mappings;
	private final Map<String, ISOCurrencyCodeEnum> synonymToIsoCurrencyCodeEnumMap;

	ElectiveAmountElectionMappingHelper(RosettaPath path, List<Mapping> mappings) {
		this.path = path;
		this.mappings = mappings;
		this.synonymToIsoCurrencyCodeEnumMap = synonymToEnumValueMap(ISOCurrencyCodeEnum.values(), ISDA_CREATE_SYNONYM_SOURCE);
	}

	Optional<ElectiveAmountElection> getElectiveAmountElection(String synonymValue, String party) {
		ElectiveAmountElection.ElectiveAmountElectionBuilder electiveAmountElectionBuilder = ElectiveAmountElection.builder();
		Money.MoneyBuilder moneyBuilder = Money.builder();

		setValueAndUpdateMappings(Path.parse(String.format("answers.partyA.%s.%s_amount", synonymValue, party)),
				(value) -> moneyBuilder.setAmount(new BigDecimal(value)), mappings, path);

		setValueAndOptionallyUpdateMappings(Path.parse(String.format("answers.partyA.%s.%s_currency", synonymValue, party)),
				(value) -> setIsoCurrency(synonymToIsoCurrencyCodeEnumMap, moneyBuilder::setCurrency, value), mappings, path);

		if (moneyBuilder.hasData()) {
			electiveAmountElectionBuilder.setAmountBuilder(moneyBuilder);
		}

		setValueAndUpdateMappings(Path.parse(String.format("answers.partyA.%s.%s_%s", synonymValue, party, synonymValue)),
				(value) -> {
					electiveAmountElectionBuilder.setParty(party);
					if (ZERO.equals(value)) {
						electiveAmountElectionBuilder.setZeroAmount(true);
					}
				}, mappings, path);

		setValueAndUpdateMappings(Path.parse(String.format("answers.partyA.%s.%s_specify", synonymValue, party)),
				electiveAmountElectionBuilder::setCustomElection, mappings, path);

		return electiveAmountElectionBuilder.hasData() ? Optional.of(electiveAmountElectionBuilder.build()) : Optional.empty();
	}
}