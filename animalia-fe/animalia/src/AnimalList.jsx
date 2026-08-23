import React, { useEffect, useState } from 'react';
import SearchBar from './SearchBar';
import * as PropTypes from 'prop-types';
import { fetchAnimalPage } from './animalApi';

SearchBar.propTypes = { onSearch: PropTypes.func };

function AnimalList() {
  const [animals, setAnimals] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setPage(0);
  }, [searchTerm]);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    fetchAnimalPage({ q: searchTerm, page, size: 24, sort: 'name,asc' })
      .then((result) => {
        if (cancelled) return;
        setAnimals(Array.isArray(result?.content) ? result.content : []);
        setTotalPages(result?.totalPages ?? 0);
      })
      .catch((error) => console.error('Error fetching data: ', error))
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => { cancelled = true; };
  }, [searchTerm, page]);

  return (
    <div>
      <SearchBar onSearch={(term) => setSearchTerm((term || '').toLowerCase())} />
      {loading && <p>Loading…</p>}
      {animals.map((animal) => (
        <div key={animal.id} className="animal">
          <div className="animal-info">
            <h3><strong>Name:</strong> {animal.name}</h3>
            <p><strong>Phylum:</strong> {animal.phylum}</p>
            <p><strong>Family:</strong> {animal.family}</p>
            <p className="animal-description"><strong>Description:</strong> {animal.description}</p>
            <p><strong>Genus:</strong> {animal.genus}</p>
            <p><strong>Species:</strong> {animal.species}</p>
          </div>
        </div>
      ))}
      {totalPages > 1 && (
        <div className="home-pagination">
          <button type="button" disabled={page <= 0} onClick={() => setPage((p) => p - 1)}>Previous</button>
          <span>Page {page + 1} / {totalPages}</span>
          <button type="button" disabled={page >= totalPages - 1} onClick={() => setPage((p) => p + 1)}>Next</button>
        </div>
      )}
    </div>
  );
}

export default AnimalList;
