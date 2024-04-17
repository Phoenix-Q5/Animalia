function SearchBar({ onSearch }) {
    return (
        <input
            type="text"
            placeholder="Search by name, genus, phylum, etc..."
            onChange={e => onSearch(e.target.value)}
            className="search-input"
        />
    );
}

export default SearchBar;