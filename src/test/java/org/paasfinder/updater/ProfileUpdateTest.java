package org.paasfinder.updater;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import spark.Spark;

public class ProfileUpdateTest {
    @BeforeClass
    public static void beforeClass() {
        UpdateService.main(null);
    }

    @AfterClass
    public static void afterClass() {
        Spark.stop();
    }
}
