package com.playlist.composite;

import com.playlist.core.Track;
import java.util.List;

/**
 * Item folha do Composite representando uma faixa individual.
 */
public class TrackItem implements MediaItem {

  private final Track track;

  /**
   * Construtor do TrackItem.
   *
   * @param track faixa associada.
   */
  public TrackItem(Track track) {
    if (track == null) {
      throw new IllegalArgumentException("A faixa não pode ser nula.");
    }
    this.track = track;
  }

  @Override
  public String getName() {
    return track.title();
  }

  @Override
  public int getDurationSeconds() {
    return track.durationSeconds();
  }

  @Override
  public int getTrackCount() {
    return 1;
  }

  @Override
  public List<Track> flatten() {
    return List.of(track);
  }

  public Track getTrack() {
    return track;
  }
}