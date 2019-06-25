package net.harmelink.vertxkubernetespoc;

import io.reactivex.Completable;
import io.reactivex.Single;
import net.harmelink.vertxkubernetespoc.api.PetsApi;
import net.harmelink.vertxkubernetespoc.model.Pet;

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
