package com.github.mainproducer;

import io.nats.client.Options;

public final class MainProducer {
	private MainProducer() {
	}

	static void main(String[] args) {
		System.out.println("main-producer starting");

		String natsUrl = System.getenv().getOrDefault("NATS_URL", "nats://localhost:4222");
		Options options = new Options.Builder().server(natsUrl).build();
		String firstServer = options.getServers().stream().findFirst().map(Object::toString).orElse("<none>");
		System.out.println("Configured NATS server: " + firstServer);
	}
}
