/*
 * Copyright 2017-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.gcp.test;

public class SpannerEmulatorHelper extends AbstractEmulatorHelper {
	String getGatingPropertyName() {
		return "it.spanner-emulator";
	}

	String getEmulatorName() {
		return "spanner";
	}

	@Override
	protected void beforeEmulatorStart() {
		// String emulatorHost = System.getenv("SPANNER_EMULATOR_HOST");
		// Assume.assumeFalse(
		// "Run this command prior to running an emulator test and set SPANNER_EMULATOR_HOST
		// environment variable:\ngcloud beta emulators spanner env-init",
		// emulatorHost == null || emulatorHost.isEmpty());
	}

	@Override
	protected void afterEmulatorStart() {
		ProcessOutcome switchToEmulator = runSystemCommand(new String[] {
				"gcloud", "config", "configurations", "activate", "emulator" });

		ProcessOutcome processOutcome = runSystemCommand(new String[] {
				"gcloud", "spanner", "instances", "create", "integration-instance", "--config=emulator-config",
				"--description=\"Test Instance\"", "--nodes=1" },
				false);

		if (processOutcome.getStatus() != 0) {
			// don't set breakpoint here
			cleanupSpannerEmulator();
			throw new RuntimeException("Creating instance failed: "
					+ String.join("\n", processOutcome.getErrors()));
		}

		// TODO: don't forget to kill the 2 spanner processes
	}

	@Override
	protected void afterEmulatorDestroyed() {
		cleanupSpannerEmulator();
	}

	private void cleanupSpannerEmulator() {
		this.killByCommand("cloud_spanner_emulator/emulator_main");
		// this.killByCommand("cloud_spanner_emulator/emulator_gateway");
	}

}
