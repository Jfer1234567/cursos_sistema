// catalogo.js - Filtrado rápido de cursos en catálogo
document.addEventListener('DOMContentLoaded', () => {
    const searchInput = document.getElementById('searchCurso');
    if (searchInput) {
        searchInput.addEventListener('input', (e) => {
            const query = e.target.value.toLowerCase().trim();
            const cards = document.querySelectorAll('.curso-card');

            cards.forEach(card => {
                const title = card.querySelector('h3').textContent.toLowerCase();
                const desc = card.querySelector('p').textContent.toLowerCase();
                if (title.includes(query) || desc.includes(query)) {
                    card.parentElement.style.display = '';
                } else {
                    card.parentElement.style.display = 'none';
                }
            });
        });
    }
});
