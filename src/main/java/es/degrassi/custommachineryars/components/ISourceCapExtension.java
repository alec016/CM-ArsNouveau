package es.degrassi.custommachineryars.components;

import com.hollingsworth.arsnouveau.api.source.ISourceCap;

public interface ISourceCapExtension extends ISourceCap {
  int removeSource(int transferRate);

  int getTransferRate();

  int addSource(int transferRate);

  int addSource(int transferRate, boolean simulate);
}
