package com.playlist.adapter;

import com.playlist.adapter.external.LegacyVinylCatalog;
import com.playlist.core.Track;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Adapter que converte os registros do {@link LegacyVinylCatalog} para {@link Track}.
 */
public class VinylCatalogAdapter implements TrackCatalog {

    private final LegacyVinylCatalog legacyCatalog;

    public VinylCatalogAdapter(LegacyVinylCatalog legacyCatalog) {
        if (legacyCatalog == null) {
            throw new IllegalArgumentException("O catálogo legado não pode ser nulo.");
        }
        this.legacyCatalog = legacyCatalog;
    }

    @Override
    public List<Track> findAll() {
        List<Track> tracks = new ArrayList<>();
        String[] records = legacyCatalog.fetchAllRecords();
        if (records != null) {
            for (String record : records) {
                parseRecord(record).ifPresent(tracks::add);
            }
        }
        return tracks;
    }

    @Override
    public Optional<Track> findById(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }

        String rawRecord = legacyCatalog.findRecordByCatalogNumber(id);
        return parseRecord(rawRecord);
    }

    private Optional<Track> parseRecord(String record) {
        if (record == null) {
            return Optional.empty();
        }

        String[] parts = record.split("\\|");
        if (parts.length != 5) {
            return Optional.empty();
        }

        String id = parts[0].trim();
        String rawTitle = parts[1].trim();
        String rawArtist = parts[2].trim();
        String rawDuration = parts[3].trim();
        String rawPremium = parts[4].trim();

        if (id.isEmpty() || rawTitle.isEmpty()) {
            return Optional.empty();
        }

        int durationSeconds;
        try {
            int durationMs = Integer.parseInt(rawDuration);
            if (durationMs < 0) {
                return Optional.empty();
            }
            durationSeconds = durationMs / 1000;
        } catch (NumberFormatException e) {
            return Optional.empty();
        }

        String title = capitalizeWords(rawTitle);
        String artist = formatArtist(rawArtist);
        boolean premium = "Y".equalsIgnoreCase(rawPremium);

        return Optional.of(new Track(id, title, artist, durationSeconds, premium));
    }

    private String capitalizeWords(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }
        String[] words = text.trim().toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                  .append(word.substring(1))
                  .append(" ");
            }
        }
        return sb.toString().trim();
    }

    private String formatArtist(String rawArtist) {
        if (rawArtist == null || rawArtist.isBlank()) {
            return "";
        }
        String[] parts = rawArtist.split(",");
        if (parts.length == 2) {
            String lastName = capitalizeWords(parts[0]);
            String firstName = capitalizeWords(parts[1]);
            return firstName + " " + lastName;
        }
        return capitalizeWords(rawArtist);
    }
}