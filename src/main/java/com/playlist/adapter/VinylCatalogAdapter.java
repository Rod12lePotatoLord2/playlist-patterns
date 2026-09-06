package com.playlist.adapter;

import com.playlist.adapter.external.LegacyVinylCatalog;
import com.playlist.core.Track;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Adaptador para integrar o catálogo de vinis legado.
 */
public class VinylCatalogAdapter implements TrackCatalog {

  private final LegacyVinylCatalog legacyCatalog;

  /**
   * Construtor do adaptador.
   *
   * @param legacyCatalog catálogo legado.
   */
  public VinylCatalogAdapter(LegacyVinylCatalog legacyCatalog) {
    if (legacyCatalog == null) {
      throw new IllegalArgumentException("O catálogo legado não pode ser nulo.");
    }
    this.legacyCatalog = legacyCatalog;
  }

  @Override
  public List<Track> findAll() {
    List<Track> tracks = new ArrayList<>();
    for (String record : legacyCatalog.fetchAllRecords()) {
      parseRecord(record).ifPresent(tracks::add);
    }
    return tracks;
  }

  @Override
  public Optional<Track> findById(String id) {
    if (id == null || id.isBlank()) {
      return Optional.empty();
    }
    String record = legacyCatalog.findRecordByCatalogNumber(id.trim());
    return parseRecord(record);
  }

  private Optional<Track> parseRecord(String record) {
    if (record == null) {
      return Optional.empty();
    }
    String[] parts = record.split("\\|");
    if (parts.length != 5) {
      return Optional.empty();
    }

    String catalogNumber = parts[0].trim();
    String rawTitle = parts[1].trim();
    String rawArtist = parts[2].trim();
    String rawDuration = parts[3].trim();
    String rawPremium = parts[4].trim();

    if (catalogNumber.isEmpty() || rawTitle.isEmpty()) {
      return Optional.empty();
    }

    int durationSeconds;
    try {
      int ms = Integer.parseInt(rawDuration);
      if (ms < 0) {
        return Optional.empty();
      }
      durationSeconds = ms / 1000;
    } catch (NumberFormatException e) {
      return Optional.empty();
    }

    String formattedTitle = formatTitle(rawTitle);
    String formattedArtist = formatArtist(rawArtist);
    boolean premium = "Y".equalsIgnoreCase(rawPremium);

    return Optional.of(new Track(catalogNumber, formattedTitle, formattedArtist,
        durationSeconds, premium));
  }

  private String formatTitle(String text) {
    if (text.isEmpty()) {
      return text;
    }
    String[] words = text.split("\\s+");
    StringBuilder sb = new StringBuilder();
    for (String word : words) {
      if (!word.isEmpty()) {
        if (!sb.isEmpty()) {
          sb.append(" ");
        }
        sb.append(Character.toUpperCase(word.charAt(0)))
            .append(word.substring(1).toLowerCase());
      }
    }
    return sb.toString();
  }

  private String formatArtist(String artistStr) {
    if (artistStr.isEmpty()) {
      return artistStr;
    }
    String[] parts = artistStr.split(",");
    if (parts.length != 2) {
      return formatTitle(artistStr);
    }
    String lastName = formatTitle(parts[0].trim());
    String firstName = formatTitle(parts[1].trim());
    return firstName + " " + lastName;
  }
}