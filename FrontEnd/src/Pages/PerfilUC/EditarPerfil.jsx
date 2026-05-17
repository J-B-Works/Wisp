import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { NavbarPrincipal } from '../../assets/NavBar/NavBar.jsx';

import './Perfil.css';

export function EditarPerfil() {
    const navigate = useNavigate();
    
    // Passo 1: Informações / Passo 2: Senha
    const [passo, setPasso] = useState(1);

    const [formData, setFormData] = useState({
        nome: '',
        idade: '',
        cep: '',
        email: '',
        interesses: []
    });

    const [senhaData, setSenhaData] = useState({
        codigo: '',
        novaSenha: '',
        confirmarSenha: ''
    });

    // Carrega os dados atuais
    useEffect(() => {
        const dados = localStorage.getItem('usuarioWisp');
        if (dados) {
            setFormData(JSON.parse(dados));
        }
    }, []);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleSenhaChange = (e) => {
        const { name, value } = e.target;
        setSenhaData({ ...senhaData, [name]: value });
    };

    const salvarAlteracoes = () => {
        if (passo === 2) {
            if (senhaData.novaSenha !== senhaData.confirmarSenha) {
                alert("As senhas não coincidem!");
                return;
            }
            alert("Senha alterada com sucesso!");
            setPasso(1);
        } else {
            localStorage.setItem('usuarioWisp', JSON.stringify(formData));
            alert("Informações atualizadas!");
            navigate('/perfiluc');
        }
    };

    return (
        /* Reutilizando as classes do PerfilUC! */
        <div className="perfil-page">
            <NavbarPrincipal mostrarBusca={false} mostrarHamburger={true} />

            <div className="perfil-container">
                
                {passo === 1 ? (
                    /* ============================== */
                    /* TELA 1: EDITAR INFORMAÇÕES     */
                    /* ============================== */
                    <div className="perfil-card">
                        <h2 style={{ textAlign: 'center' }}>Editar Informações:</h2>
                        
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                            <div style={estilos.grupoInput}>
                                <label style={estilos.label}>Nome:</label>
                                <input type="text" name="nome" value={formData.nome} onChange={handleChange} style={estilos.input} />
                            </div>

                            <div style={{ display: 'flex', gap: '15px' }}>
                                <div style={{ ...estilos.grupoInput, width: '100px' }}>
                                    <label style={estilos.label}>Idade:</label>
                                    <input type="number" name="idade" value={formData.idade} onChange={handleChange} style={estilos.input} />
                                </div>
                                <div style={{ ...estilos.grupoInput, flex: 1 }}>
                                    <label style={estilos.label}>CEP:</label>
                                    <input type="text" name="cep" value={formData.cep} onChange={handleChange} style={estilos.input} />
                                </div>
                            </div>

                            <div style={estilos.grupoInput}>
                            <label style={estilos.label}>Email:</label>
                            <span style={{ fontSize: '1.1rem', color: '#555', padding: '10px 5px', fontWeight: '500' }}>
                            {formData.email}
                            </span>
                            </div>

                            {/* Botão de ir para a senha disfarçado de input */}
                            <div style={{ ...estilos.grupoInput, marginTop: '10px' }}>
                                <label style={estilos.label}>Senha:</label>
                                <button onClick={() => setPasso(2)} style={estilos.btnSenha}>
                                    🔑 Redefinir Senha
                                </button>
                            </div>
                        </div>
                    </div>
                ) : (
                    /* ============================== */
                    /* TELA 2: REDEFINIR SENHA        */
                    /* ============================== */
                    <div className="perfil-card" style={{ backgroundColor: '#F2A4B3' /* Muda a cor do card para rosa! */ }}>
                        <h2 style={{ textAlign: 'center' }}>Redefinição de Senha</h2>
                        
                        <p style={{ textAlign: 'center', marginBottom: '20px', color: 'var(--bordas)', fontWeight: '500' }}>
                            Um código foi enviado ao email cadastrado.
                        </p>
                        

                        <div style={{ display: 'flex', flexDirection: 'column', gap: '15px', alignItems: 'center' }}>
                            <div style={{ ...estilos.grupoInput, width: '200px', alignItems: 'center' }}>
                                <label style={estilos.label}>Insira o código:</label>
                                <input type="text" name="codigo" value={senhaData.codigo} onChange={handleSenhaChange} style={{...estilos.input, textAlign: 'center', letterSpacing: '2px', fontWeight: 'bold'}} />
                            </div>

                            <div style={{ width: '100%', display: 'flex', gap: '15px', marginTop: '10px' }}>
                                <div style={{ ...estilos.grupoInput, flex: 1 }}>
                                    <label style={estilos.label}>Nova Senha:</label>
                                    <input type="password" name="novaSenha" value={senhaData.novaSenha} onChange={handleSenhaChange} style={estilos.input} />
                                </div>
                                <div style={{ ...estilos.grupoInput, flex: 1 }}>
                                    <label style={estilos.label}>Confirmar Senha:</label>
                                    <input type="password" name="confirmarSenha" value={senhaData.confirmarSenha} onChange={handleSenhaChange} style={estilos.input} />
                                </div>
                            </div>
                        </div>
                    </div>
                )}

                {/* BOTÕES DE SALVAR E VOLTAR */}
                <div style={{ display: 'flex', justifyContent: 'center', gap: '20px', marginTop: '20px' }}>
                    <button 
                        onClick={() => passo === 1 ? navigate('/perfiluc') : setPasso(1)} 
                        style={{ ...estilos.btnAcao, backgroundColor: 'var(--accent-amarelo)' }}
                    >
                        Voltar
                    </button>
                    <button 
                        onClick={salvarAlteracoes} 
                        style={{ ...estilos.btnAcao, backgroundColor: 'var(--accent-rosa)' }}
                    >
                        Salvar
                    </button>
                </div>

            </div>
        </div>
    );
}

// Estilos apenas para os inputs e botões dessa tela não precisarem de um CSS novo!
const estilos = {
    grupoInput: {
        display: 'flex',
        flexDirection: 'column',
        gap: '5px'
    },
    label: {
        fontWeight: 'bold',
        color: 'var(--bordas)',
        fontSize: '1.1rem'
    },
    input: {
        padding: '12px 15px',
        borderRadius: '10px',
        border: '2px solid var(--bordas)',
        fontSize: '1rem',
        outline: 'none',
        fontFamily: 'inherit'
    },
    btnSenha: {
        padding: '12px 15px',
        borderRadius: '10px',
        border: '2px dashed var(--bordas)',
        backgroundColor: 'rgba(255,255,255,0.5)',
        fontSize: '1rem',
        fontWeight: 'bold',
        cursor: 'pointer',
        textAlign: 'left',
        color: 'var(--bordas)'
    },
    btnAcao: {
        padding: '12px 40px',
        border: '2px solid var(--bordas)',
        borderRadius: '25px',
        fontSize: '1.1rem',
        fontWeight: 'bold',
        cursor: 'pointer',
        color: 'var(--bordas)'
    }
};