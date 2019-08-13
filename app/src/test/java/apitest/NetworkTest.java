package apitest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ericdiaz.program.gotennachallenge.api.PlacesService;
import ericdiaz.program.gotennachallenge.api.RetrofitServiceGenerator;
import ericdiaz.program.gotennachallenge.model.Place;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.TestObserver;

/**
 * A Test class for testing network connection and responses
 * <p>
 * Created 8/13/19
 *
 * @author Eric Diaz
 */

public class NetworkTest {

    private PlacesService placesService;
    private CompositeDisposable compositeDisposable;

    @Before
    public void setUp() {
        placesService = RetrofitServiceGenerator.getPlacesService();
        compositeDisposable = new CompositeDisposable();
    }

    @Test
    public void testBasicConnectionToEndPoint() {
        //given
        final String verifiedPlaceName = "Cadman Plaza Park";

        //when
        TestObserver<Place[]> testObserver = placesService.getPlaces().test();

        //then
        try {
            compositeDisposable.add(testObserver
              .await()
              .assertValue(places -> places[0].getName().equals(verifiedPlaceName)));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        compositeDisposable.dispose();
    }
}
