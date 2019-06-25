package net.harmelink.vertxkubernetespoc;

import io.reactivex.Completable;
import io.reactivex.Single;
import org.openapitools.server.api.model.Pet;
import org.openapitools.server.api.verticle.PetsApi;

import java.util.List;

public class PetsService implements PetsApi {

    @Override
    public Completable createPets() {
        return null;
    }

    @Override
    public Single<List<Pet>> listPets(final Integer limit) {
        return null;
    }

    @Override
    public Single<List<Pet>> showPetById(final String petId) {
        return null;
    }
}
