package com.playlist.composite;

import com.playlist.core.Track;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Composite do padrão Composite: uma playlist que pode conter faixas e outras playlists.
 */
public class PlaylistNode implements MediaItem {

    private final String name;
    private final List<MediaItem> children = new ArrayList<>();

    /**
     * Cria uma playlist vazia.
     *
     * @param name nome da playlist. Não pode ser nulo nem em branco.
     * @throws IllegalArgumentException se o nome for nulo ou em branco.
     */
    public PlaylistNode(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("O nome da playlist não pode ser nulo ou vazio.");
        }
        this.name = name;
    }

    /**
     * Adiciona um item ao final da playlist.
     *
     * @param item item a ser adicionado.
     * @return a própria playlist, permitindo encadear chamadas.
     * @throws IllegalArgumentException se o item for nulo, for a própria playlist ou contiver a própria playlist (o que criaria um ciclo).
     */
    public PlaylistNode add(MediaItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item não pode ser nulo.");
        }
        if (item == this) {
            throw new IllegalArgumentException("Uma playlist não pode conter a si mesma.");
        }
        if (item instanceof PlaylistNode node && node.contains(this)) {
            throw new IllegalArgumentException("Adicionar este item criaria um ciclo.");
        }
        children.add(item);
        return this;
    }

    /**
     * Remove um filho direto da playlist.
     *
     * @param item item a ser removido.
     * @return {@code true} se o item era filho direto e foi removido.
     */
    public boolean remove(MediaItem item) {
        return children.remove(item);
    }

    /**
     * Lista os filhos diretos da playlist.
     *
     * @return uma lista imutável com os filhos, na ordem de inserção.
     */
    public List<MediaItem> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /**
     * Verifica se o item está em qualquer nível abaixo desta playlist.
     *
     * @param item item procurado.
     * @return {@code true} se o item for filho direto ou descendente.
     */
    public boolean contains(MediaItem item) {
        for (MediaItem child : children) {
            if (child.equals(item)) {
                return true;
            }
            if (child instanceof PlaylistNode node && node.contains(item)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getDurationSeconds() {
        return children.stream()
                .mapToInt(MediaItem::getDurationSeconds)
                .sum();
    }

    @Override
    public int getTrackCount() {
        return children.stream()
                .mapToInt(MediaItem::getTrackCount)
                .sum();
    }

    @Override
    public List<Track> flatten() {
        List<Track> tracks = new ArrayList<>();
        for (MediaItem child : children) {
            tracks.addAll(child.flatten());
        }
        return tracks;
    }
}
