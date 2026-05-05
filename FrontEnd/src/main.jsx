import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter, Routes, Route } from 'react-router-dom' 
import './index.css'

// Importando as duas páginas
import { Home } from './Pages/Home/Index.jsx'
import { Login } from './Pages/Login/Index.jsx' // <--- Puxando o seu arquivo de Login
import Feed from './Pages/Feed/Index.jsx'
import { Cadastro } from './Pages/Cadastro/Index.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/Login" element={<Login />} />
        <Route path="/feed" element={<Feed />} />
        <Route path="/cadastro" element={<Cadastro />} />
      </Routes>
    </BrowserRouter>
  </StrictMode>,
)