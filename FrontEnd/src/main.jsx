import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter, Routes, Route } from 'react-router-dom' 
import './index.css'

// Importando as duas páginas
import { Home } from './Pages/Home/Home.jsx'
import { Login } from './Pages/Login/Login.jsx' // <--- Puxando o seu arquivo de Login
import Feed from './Pages/Feed/Feed.jsx'
import { Cadastro } from './Pages/Cadastro/Cadastro.jsx'
import PerfilUC from './Pages/PerfilUC/Perfil.jsx'
import { DetalhesAtividade } from './Pages/DetalhesAtvdds/DetalhesAtividade.jsx'
import { WIP } from './Pages/WIP/WIP.jsx';
import { Favoritos } from './Pages/Favoritos/Favoritos.jsx';
import { EditarPerfil } from './Pages/PerfilUC/EditarPerfil.jsx';

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/Login" element={<Login />} />
        <Route path="/feed" element={<Feed />} />
        <Route path="/cadastro" element={<Cadastro />} />
        <Route path="/perfiluc" element={<PerfilUC />} />
        <Route path="/atividade/:id" element={<DetalhesAtividade />} />
        <Route path="/em-breve" element={<WIP />} />
        <Route path="/favoritos" element={<Favoritos />} />
        <Route path="/editar-perfil" element={<EditarPerfil />} />
        
      </Routes>
    </BrowserRouter>
  </StrictMode>,
)