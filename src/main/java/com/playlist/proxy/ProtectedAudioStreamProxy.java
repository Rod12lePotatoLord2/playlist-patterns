package com.playlist.proxy;

import com.playlist.core.AccessDeniedException;
import com.playlist.core.Subscription;
import com.playlist.core.Track;
import java.util.function.Supplier;

/**
 * Proxy que controla o acesso ao {@link RemoteAudioStream}.
 */
public class ProtectedAudioStreamProxy implements AudioStream {

    private final Track track;
    private final Subscription plan;
    private final Supplier<AudioStream> loader;
    private AudioStream realStream;
    private byte[] cachedBytes;

    public ProtectedAudioStreamProxy(Track track, Subscription plan, Supplier<AudioStream> loader) {
        if (track == null || plan == null || loader == null) {
            throw new IllegalArgumentException("Nenhum parâmetro pode ser nulo.");
        }
        this.track = track;
        this.plan = plan;
        this.loader = loader;
    }

    public ProtectedAudioStreamProxy(Track track, Subscription plan) {
        this(track, plan, () -> new RemoteAudioStream(track));
    }

    public boolean isLoaded() {
        return realStream != null;
    }

    @Override
    public String getTrackId() {
        return track.id();
    }

    @Override
    public byte[] readBytes() {
        if (track.premium() && plan == Subscription.FREE) {
            throw new AccessDeniedException("Faixas premium exigem assinatura paga.");
        }

        if (cachedBytes == null) {
            if (realStream == null) {
                realStream = loader.get();
            }
            cachedBytes = realStream.readBytes();
        }

        return cachedBytes.clone();
    }
}