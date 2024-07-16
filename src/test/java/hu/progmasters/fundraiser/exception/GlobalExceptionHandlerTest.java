package hu.progmasters.fundraiser.exception;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.mockito.MockitoAnnotations.openMocks;

public class GlobalExceptionHandlerTest {

    @Mock
    private MethodArgumentNotValidException exception;

    @InjectMocks
    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

}
