package com.playlist.facade;

import com.playlist.adapter.TrackCatalog;
import com.playlist.composite.PlaylistNode;
import com.playlist.composite.TrackItem;
import com.playlist.core.Subscription;
import com.playlist.core.Track;
import com.playlist.core.TrackNotFoundException;
import com.playlist.decorator.AudioTrack;
import com.playlist.decorator.FadeInEffect;
import com.playlist.decorator.RawAudioTrack;
import com.playlist.decorator.VolumeEffect;
import com.playlist.proxy.AudioStream;
import com.playlist.proxy.ProtectedAudioStreamProxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Fachada para orquestração de streaming, playlists e efeitos.
 */
public class PlaylistFacade {

  private final TrackCatalog catalog;
  private final Subscription plan;
  private final Map<String, AudioStream> streamCache = new HashMap<>();

  /**
   * Construtor da Fachada.
   *
   * @param catalog catálogo de faixas.
   * @param plan plano de assinatura.
   */
  public PlaylistFacade(TrackCatalog catalog, Subscription plan) {
    if (catalog == null || plan == null) {
      throw new IllegalArgumentException("Catálogo e plano não podem ser nulos.");
    }
    this.catalog = catalog;
    this.plan = plan;
  }

  /**
   * Monta a biblioteca inteira em uma playlist.
   *
   * @param name nome da playlist.
   * @return playlist montada.
   */
  public PlaylistNode buildLibrary(String name) {
    PlaylistNode library = new PlaylistNode(name);
    for (Track track : catalog.findAll()) {
      library.add(new TrackItem(track));
    }
    return library;
  }

  /**
   * Executa a reprodução de uma faixa pelo ID.
   *
   * @param trackId ID da faixa.
   * @return bytes de áudio.
   */
  public byte[] listen(String trackId) {
    AudioStream stream = streamCache.computeIfAbsent(trackId, id -> {
      Track track = catalog.findById(id)
          .orElseThrow(() -> new TrackNotFoundException("Faixa não encontrada: " + id));
      return new ProtectedAudioStreamProxy(track, plan);
    });

    return stream.readBytes();
  }

  /**
   * Gera uma prévia da faixa com efeitos.
   *
   * @param trackId ID da faixa.
   * @param volume fator de volume.
   * @param fadeInSamples quantidade de amostras de fade in.
   * @return faixa decorada.
   */
  public AudioTrack preview(String trackId, double volume, int fadeInSamples) {
    Track track = catalog.findById(trackId)
        .orElseThrow(() -> new TrackNotFoundException("Faixa não encontrada: " + trackId));

    byte[] bytes = listen(trackId);

    double[] samples = new double[bytes.length];
    for (int i = 0; i < bytes.length; i++) {
      samples[i] = bytes[i] / 128.0;
    }

    AudioTrack raw = new RawAudioTrack(track.title(), samples);
    AudioTrack withVolume = new VolumeEffect(raw, volume);
    return new FadeInEffect(withVolume, fadeInSamples);
  }
}