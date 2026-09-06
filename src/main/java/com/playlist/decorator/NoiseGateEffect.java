package com.playlist.decorator;

import java.util.Locale;

/**
 * Efeito de Noise Gate.
 */
public final class NoiseGateEffect extends AudioEffect {

  private final double threshold;

  /**
   * Cria o efeito Noise Gate.
   *
   * @param wrapped áudio decorado.
   * @param threshold limiar de corte.
   */
  public NoiseGateEffect(AudioTrack wrapped, double threshold) {
    super(wrapped);
    if (threshold < 0) {
      throw new IllegalArgumentException("O limiar não pode ser negativo.");
    }
    this.threshold = threshold;
  }

  @Override
  protected String describe() {
    return String.format(Locale.ROOT, "noiseGate(%.2f)", threshold);
  }

  @Override
  public double[] getSamples() {
    double[] original = wrapped.getSamples();
    double[] result = new double[original.length];

    for (int i = 0; i < original.length; i++) {
      if (Math.abs(original[i]) < threshold) {
        result[i] = 0.0;
      } else {
        result[i] = original[i];
      }
    }

    return result;
  }
}