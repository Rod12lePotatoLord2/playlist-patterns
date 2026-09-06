package com.playlist.decorator;

/**
 * Decorator abstrato: envolve outro {@link AudioTrack} e acrescenta um efeito.
 */
public abstract class AudioEffect implements AudioTrack {

    protected final AudioTrack wrapped;

    protected AudioEffect(AudioTrack wrapped) {
        if (wrapped == null) {
            throw new IllegalArgumentException("O áudio decorado não pode ser nulo.");
        }
        this.wrapped = wrapped;
    }

    protected abstract String describe();

    @Override
    public String getTitle() {
        return wrapped.getTitle();
    }

    @Override
    public String getEffectChain() {
        String baseChain = wrapped.getEffectChain();
        if (baseChain == null || baseChain.isBlank() || "raw".equalsIgnoreCase(baseChain)) {
            return describe();
        }
        return baseChain + " -> " + describe();
    }
}