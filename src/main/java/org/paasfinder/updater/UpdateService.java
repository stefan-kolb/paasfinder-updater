package org.paasfinder.updater;

import org.paasfinder.updater.resources.ProfileResource;

public class UpdateService {
    public static void main(String[] args) {
        new ProfileResource();
    }

    public static int getAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567;
    }
}