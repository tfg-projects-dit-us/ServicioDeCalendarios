package guardians.controllers.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import guardians.controllers.AllowedShiftsController;
import guardians.controllers.DoctorController;
import guardians.controllers.RootController;
import guardians.controllers.ShiftConfigurationController;
import guardians.model.dtos.general.ShiftConfigurationPublicDTO;

/**
 * DoctorAssembler is responsible for converting a
 * {@link ShiftConfigurationPublicDTO} object to its {@link EntityModel}
 * representation. This is, adding links to it.
 * 
 * @author miggoncan
 *
 */
@Component
public class ShiftConfigurationAssembler
		implements RepresentationModelAssembler<ShiftConfigurationPublicDTO, EntityModel<ShiftConfigurationPublicDTO>> {

	@Value("${api.links.root}")
	private String rootLink;

	@Value("${api.links.shiftconfs}")
	private String shiftConfsLink;

	@Value("${api.links.doctor}")
	private String doctorLink;

	@Value("${api.links.allowedshifts}")
	private String allowedShiftsLink;

	@Override
	public EntityModel<ShiftConfigurationPublicDTO> toModel(ShiftConfigurationPublicDTO entity) {
		return new EntityModel<ShiftConfigurationPublicDTO>(entity,
				linkTo(methodOn(ShiftConfigurationController.class).getShitfConfiguration(entity.getDoctorId()))
						.withSelfRel(),
				linkTo(methodOn(ShiftConfigurationController.class).getShitfConfigurations()).withRel(shiftConfsLink),
				linkTo(methodOn(DoctorController.class).getDoctor(entity.getDoctorId())).withRel(doctorLink),
				linkTo(methodOn(AllowedShiftsController.class).getAllowedShifts()).withRel(allowedShiftsLink));
	}

	@Override
	public CollectionModel<EntityModel<ShiftConfigurationPublicDTO>> toCollectionModel(
			Iterable<? extends ShiftConfigurationPublicDTO> entities) {
		List<EntityModel<ShiftConfigurationPublicDTO>> shiftConfs = new LinkedList<>();
		for (ShiftConfigurationPublicDTO entity : entities) {
			shiftConfs.add(this.toModel(entity));
		}
		return new CollectionModel<>(shiftConfs,
				linkTo(methodOn(ShiftConfigurationController.class).getShitfConfigurations()).withSelfRel(),
				linkTo(methodOn(RootController.class).getRootLinks()).withRel(rootLink));
	}
}
