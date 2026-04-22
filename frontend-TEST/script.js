//CODIGO TESTE DE JS PARA CLICAR NAS ESTRELAS ---------------------------------------------
// Aguarda o HTML inteiro ser carregado na tela
document.addEventListener('DOMContentLoaded', () => {
    
    // Seleciona todas as estrelas
    const stars = document.querySelectorAll('.star-icon');

    // Se não encontrou nenhuma estrela, avisa no console
    if (stars.length === 0) {
        console.log("Nenhuma estrela encontrada. Verifique se a classe 'star-icon' está no HTML.");
    }

    stars.forEach(star => {
        star.addEventListener('click', function() {
            // Alterna a cor (CSS)
            this.classList.toggle('active');
            
            // Alterna o preenchimento (Phosphor Icons)
            this.classList.toggle('ph');
            this.classList.toggle('ph-fill');
        });
    });

});

//----------------------------------------------------------------------------------