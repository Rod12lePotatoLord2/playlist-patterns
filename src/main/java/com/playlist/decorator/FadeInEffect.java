package com.playlist.decorator;

/**
 * Efeito de Fade In.
 */
public final class FadeInEffect extends AudioEffect {

  private final int sampleCount;

  /**
   * Cria o efeito de Fade In.
   *
   * @param wrapped áudio decorado.
   * @param sampleCount quantidade de amostras na rampa.
   */
  public FadeInEffect(AudioTrack wrapped, int sampleCount) {
    super(wrapped);
    if (sampleCount < 0) {
      throw new IllegalArgumentException("A quantidade de amostras não pode ser negativa.");
    }
    this.sampleCount = sampleCount;
  }

  @Override
  protected String describe() {
    return "fadeIn(" + sampleCount + ")";
  }

  @Override
  public double[] getSamples() {
    double[] original = wrapped.getSamples();
    double[] result = original.clone();

    int limit = Math.min(sampleCount, original.length);
    if (limit > 0) {
      for (int i = 0; i < limit; i++) {
        double multiplier = (double) i / limit;
        result[i] = original[i] * multiplier;
      }
    }

    return result;
  }
}