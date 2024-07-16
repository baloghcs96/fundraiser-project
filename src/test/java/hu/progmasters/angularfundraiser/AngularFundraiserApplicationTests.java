package hu.progmasters.angularfundraiser;

import hu.progmasters.fundraiser.AngularFundraiserApplication;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AngularFundraiserApplicationTests {

    @Test
    void main() {
        AngularFundraiserApplication.main(new String[]{});
        assertThat(true).isTrue();
    }
}