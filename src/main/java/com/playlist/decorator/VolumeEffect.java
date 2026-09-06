package com.playlist.decorator;

import java.util.Locale;

/**
 * Efeito de ajuste de volume.
 */
public final class VolumeEffect extends AudioEffect {

  private final double factor;

  /**
   * Cria o efeito de volume.
   *
   * @param wrapped áudio decorado.
   * @param factor multiplicador de volume.
   */
  public VolumeEffect(AudioTrack wrapped, double factor) {
    super(wrapped);
    if (factor < 0) {
      throw new IllegalArgumentException("O fator não pode ser negativo.");
    }
    this.factor = factor;
  }

  @Override
  protected String describe() {
    return String.format(Locale.ROOT, "volume(%.1f)", factor);
  }

  @Override
  public double[] getSamples() {
    double[] original = wrapped.getSamples();
    double[] result = new double[original.length];

    for (int i = 0; i < original.length; i++) {
      double val = original[i] * factor;
      if (val > 1.0) {
        val = 1.0;
      }
      if (val < -1.0) {
        val = -1.0;
      }
      result[i] = val;
    }

    return result;
  }
}