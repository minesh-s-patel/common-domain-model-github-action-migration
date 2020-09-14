package org.isda.cdm.processor;

import static org.isda.cdm.processor.CdmMappingProcessorUtils.PARTIES;

import java.util.List;
import java.util.Optional;

import com.regnosys.rosetta.common.translation.MappingContext;
import com.regnosys.rosetta.common.translation.MappingProcessor;
import com.regnosys.rosetta.common.translation.Path;
import com.rosetta.model.lib.RosettaModelObjectBuilder;
import com.rosetta.model.lib.path.RosettaPath;

import cdm.base.staticdata.party.PartyContactInformation;
import cdm.base.staticdata.party.metafields.ReferenceWithMetaParty;
import cdm.legalagreement.csa.ContactElection;

public class ContactElectionMappingProcessor extends MappingProcessor {
    public ContactElectionMappingProcessor(RosettaPath modelPath, List<Path> synonymPaths, MappingContext mappingContext) {
        super(modelPath, synonymPaths, mappingContext);
    }

    @Override
    public void map(Path synonymPath, RosettaModelObjectBuilder builder, RosettaModelObjectBuilder parent) {
        ContactElection.ContactElectionBuilder contactElectionBuilder = (ContactElection.ContactElectionBuilder) builder;
        PARTIES.forEach(party -> getPartyContactInformation(synonymPath, party).ifPresent(contactElectionBuilder::addPartyElection));
    }

    private Optional<PartyContactInformation> getPartyContactInformation(Path synonymPath, String party) {
        PartyContactInformation.PartyContactInformationBuilder partyContactInformationBuilder = PartyContactInformation.builder();
        setValueAndUpdateMappings(synonymPath.addElement(party + "_specify"), address -> partyContactInformationBuilder.setPartyReference(ReferenceWithMetaParty.builder().setExternalReference(party).build()).setAddress(address));
        return partyContactInformationBuilder.hasData() ? Optional.of(partyContactInformationBuilder.build()) : Optional.empty();
    }
}
