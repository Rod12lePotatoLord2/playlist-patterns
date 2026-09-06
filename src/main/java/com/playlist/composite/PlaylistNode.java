package com.playlist.composite;

import com.playlist.core.Track;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Nó do Composite representando uma playlist que contém outros MediaItems.
 */
public class PlaylistNode implements MediaItem {

  private final String name;
  private final List<MediaItem> children = new ArrayList<>();

  /**
   * Construtor da PlaylistNode.
   *
   * @param name nome da playlist.
   */
  public PlaylistNode(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Nome da playlist é inválido.");
    }
    this.name = name;
  }

  /**
   * Adiciona um item à playlist.
   *
   * @param item item a ser adicionado.
   * @return a própria playlist.
   */
  public PlaylistNode add(MediaItem item) {
    if (item == null) {
      throw new IllegalArgumentException("O item não pode ser nulo.");
    }
    if (item == this) {
      throw new IllegalArgumentException("Playlist não pode conter a si mesma.");
    }
    if (item instanceof PlaylistNode node && node.contains(this)) {
      throw new IllegalArgumentException("A adição criaria um ciclo.");
    }
    children.add(item);
    return this;
  }

  public boolean remove(MediaItem item) {
    return children.remove(item);
  }

  public List<MediaItem> getChildren() {
    return Collections.unmodifiableList(children);
  }

  /**
   * Verifica se o item está contido na playlist recursivamente.
   *
   * @param item item buscado.
   * @return true se encontrado.
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
    List<Track> result = new ArrayList<>();
    for (MediaItem child : children) {
      result.addAll(child.flatten());
    }
    return Collections.unmodifiableList(result);
  }
}