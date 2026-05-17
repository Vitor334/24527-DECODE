package org.firstinspires.ftc.teamcode.beta.utils;

import com.qualcomm.robotcore.util.ElapsedTime;
import java.util.function.BooleanSupplier;

/**
 * A utility class for handling timeouts within a Command.
 * It resets itself when the check() is first called after being idle or manually reset.
 */
public class Timeout {
    private final ElapsedTime timer = new ElapsedTime();
    private final double seconds;
    private final BooleanSupplier condition;
    private boolean running = false;

    /**
     * Constructor for Timeout.
     *
     * @param condition The condition to check.
     * @param seconds   The timeout in seconds.
     */
    public Timeout(BooleanSupplier condition, double seconds) {
        this.condition = condition;
        this.seconds = seconds;
    }

    /**
     * Checks if the condition is met or the timeout has elapsed.
     * Resets the timer on the first call after creation or manual reset.
     *
     * @return true if the condition is met or timeout is reached, false otherwise.
     */
    public boolean check() {
        if (!running) {
            timer.reset();
            running = true;
        }
        
        boolean isDone = condition.getAsBoolean() || timer.seconds() >= seconds;
        
        if (isDone) {
            running = false;
        }
        
        return isDone;
    }
}
